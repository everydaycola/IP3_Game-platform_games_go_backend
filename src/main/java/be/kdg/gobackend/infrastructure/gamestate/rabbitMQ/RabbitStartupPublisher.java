package be.kdg.gobackend.infrastructure.gamestate.rabbitMQ;

import be.kdg.gobackend.api.dto.registeration.FullGameDto;
import be.kdg.gobackend.config.rabbitMQ.RabbitMQProperties;
import be.kdg.gobackend.infrastructure.gamestate.rabbitMQ.messages.RegisterGameMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class RabbitStartupPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    public RabbitStartupPublisher(RabbitTemplate rabbitTemplate, RabbitMQProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void publishStartupEvent() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("go.json")) {
            if (is == null) {
                log.error("go.json not found in resources");
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            FullGameDto goDto = objectMapper.readValue(is, FullGameDto.class);
            FullGameDto updatedDto = new FullGameDto(
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
            RegisterGameMessage message = new RegisterGameMessage(updatedDto);

            rabbitTemplate.convertAndSend(
                    properties.getExchangeName(),
                    properties.getRegisterGameBinding(),
                    message
            );

            log.info("Startup game message sent to RabbitMQ: {}", goDto);

        } catch (IOException e) {
            log.error("Failed to read go.json", e);
        }
    }
}