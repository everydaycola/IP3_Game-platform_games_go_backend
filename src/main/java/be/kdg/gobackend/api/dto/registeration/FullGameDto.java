package be.kdg.gobackend.api.dto.registeration;


import java.util.List;
import java.util.UUID;

public record FullGameDto(
        UUID id,
        String name,
        String description,
        double price,
        String image,
        String icon,
        String genre,
        String url,
        List<AchievementDto> achievements
) {


}
