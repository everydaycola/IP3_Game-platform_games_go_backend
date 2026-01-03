package be.kdg.gobackend.infrastructure.gamestate.rabbitMQ;

import be.kdg.gobackend.api.dto.registeration.FullGameDto;
import be.kdg.gobackend.config.rabbitMQ.RabbitMQProperties;
import be.kdg.gobackend.infrastructure.gamestate.rabbitMQ.messages.RegisterGameMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class RabbitStartupPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;
    private final UrlChecker urlChecker;
    private final TaskScheduler taskScheduler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @EventListener(ApplicationReadyEvent.class)
    public void publishStartupEvent() {
        final var updatedDto = loadAndBuildDto();
        if (updatedDto == null) return;

        final var futureRef = new AtomicReference<ScheduledFuture<?>>();

        final var future = taskScheduler.scheduleWithFixedDelay(() -> {
            try {
                if (!urlChecker.isUrlReachable(properties.getInternalGameUrl())) {
                    log.warn("Game not registered yet; url not reachable internally: {}, external is {}", properties.getInternalGameUrl(), properties.getExternalGameUrl());
                    return;
                }

                rabbitTemplate.convertAndSend(
                        properties.getExchangeName(),
                        properties.getRegisterGameBinding(),
                        new RegisterGameMessage(updatedDto)
                );
                log.info("Startup game message sent to RabbitMQ: {}", updatedDto);

                final var f = futureRef.get();
                if (f != null) f.cancel(false);

            } catch (AmqpException e) {
                log.error("Error while trying to register startup game (will retry)", e);
            }
        }, Duration.ofSeconds(5));

        futureRef.set(future);
    }

    private FullGameDto loadAndBuildDto() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("go.json")) {
            if (is == null) {
                log.error("go.json not found in resources");
                return null;
            }

            final var goDto = objectMapper.readValue(is, FullGameDto.class);
            return new FullGameDto(
                    goDto.id(),
                    goDto.name(),
                    goDto.maxPlayerCount(),
                    goDto.aiStartGameEndpoint(),
                    goDto.startGameEndpoint(),
                    goDto.description(),
                    goDto.price(),
                    goDto.image(),
                    goDto.icon(),
                    goDto.genre(),
                    properties.getExternalGameUrl(),
                    goDto.achievements(),
                    goDto.configurableSettings()
            );
        } catch (IOException e) {
            log.error("Failed to read go.json", e);
            return null;
        }
    }
}