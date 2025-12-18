package be.kdg.gobackend.infrastructure.gamestate.rabbitMQ.messages;

import java.util.UUID;

public record AchievementMessageDto(UUID userId, UUID achievementId) {
}
