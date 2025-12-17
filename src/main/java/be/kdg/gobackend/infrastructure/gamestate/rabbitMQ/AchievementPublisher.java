package be.kdg.gobackend.infrastructure.gamestate.rabbitMQ;

import be.kdg.gobackend.config.rabbitMQ.RabbitMQProperties;
import be.kdg.gobackend.domain.achievements.Achievement;
import be.kdg.gobackend.infrastructure.gamestate.rabbitMQ.messages.AchievementMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AchievementPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    // The method now takes the Enum, not a String ID
    public void unlock(UUID playerId, Achievement achievement) {
        log.info("Sending achievement unlocked message for player {} and achievement {} : {} ({})", playerId, achievement.getTitle(), achievement.getDescription(), achievement.getId());
        rabbitTemplate.convertAndSend(
                properties.getExchangeName(),
                properties.getUnlockAchievementBinding(),
                new AchievementMessageDto(
                    playerId,
                    achievement.getId()
                )
        );
    }
}