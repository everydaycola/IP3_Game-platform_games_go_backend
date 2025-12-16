package be.kdg.gobackend.api.dto.registeration;

import java.util.UUID;

public record AchievementDto(UUID id, String name, String description) {
}
