package be.kdg.gobackend.infrastructure.gamestate.rabbitMQ;

import be.kdg.gobackend.api.dto.registeration.AchievementDto;
import be.kdg.gobackend.config.rabbitMQ.RabbitMQConfig;
import be.kdg.gobackend.config.rabbitMQ.RabbitMQProperties;
import be.kdg.gobackend.domain.achievements.Achievement;
import be.kdg.gobackend.infrastructure.gamestate.rabbitMQ.messages.AchievementMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AchievementPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    // The method now takes the Enum, not a String ID
    public void unlock(UUID playerId, Achievement achievement) {

        rabbitTemplate.convertAndSend(
                properties.getExchangeName(),
                "game.achievement.unlocked",
                new AchievementMessageDto(
                    playerId,
                    achievement.getId()
                )
        );
    }
}