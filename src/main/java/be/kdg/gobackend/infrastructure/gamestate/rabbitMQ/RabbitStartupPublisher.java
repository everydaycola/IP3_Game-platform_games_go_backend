package be.kdg.gobackend.infrastructure.gamestate.rabbitMQ;

import be.kdg.gobackend.api.dto.registeration.FullGameDto;
import be.kdg.gobackend.config.rabbitMQ.RabbitMQProperties;
import be.kdg.gobackend.infrastructure.gamestate.rabbitMQ.messages.RegisterGameMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
public class RabbitStartupPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;
    private final UrlChecker urlChecker;
    private final TaskScheduler taskScheduler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RabbitStartupPublisher(RabbitTemplate rabbitTemplate, RabbitMQProperties properties, UrlChecker urlChecker, TaskScheduler taskScheduler) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
        this.urlChecker = urlChecker;
        this.taskScheduler = taskScheduler;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void publishStartupEvent() {
        FullGameDto updatedDto = loadAndBuildDto();
        if (updatedDto == null) return;

        AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();

        ScheduledFuture<?> future = taskScheduler.scheduleWithFixedDelay(() -> {
            try {
                if (!urlChecker.isUrlReachable(updatedDto.url())) {
                    log.warn("Game not registered yet; url not reachable: {}", updatedDto.url());
                    return;
                }

                RegisterGameMessage message = new RegisterGameMessage(updatedDto);
                rabbitTemplate.convertAndSend(
                        properties.getExchangeName(),
                        properties.getRegisterGameBinding(),
                        message
                );
                log.info("Startup game message sent to RabbitMQ: {}", updatedDto);

                ScheduledFuture<?> f = futureRef.get();
                if (f != null) f.cancel(false);
            } catch (Exception e) {
                // Don’t kill the scheduler thread; just log and let it retry on next tick.
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

            FullGameDto goDto = objectMapper.readValue(is, FullGameDto.class);
            return new FullGameDto(
                    goDto.id(),
                    goDto.name(),
                    goDto.description(),
                    goDto.price(),
                    goDto.image(),
                    goDto.icon(),
                    goDto.genre(),
                    properties.getGameUrl(),
                    goDto.achievements()
            );
        } catch (IOException e) {
            log.error("Failed to read go.json", e);
            return null;
        }
    }
}