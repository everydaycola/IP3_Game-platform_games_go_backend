package be.kdg.gobackend.infrastructure.gamestate.ai;

import be.kdg.gobackend.api.dto.GameStateDto;
import be.kdg.gobackend.domain.repository.AiCatalog;
import be.kdg.gobackend.infrastructure.gamestate.ai.dtos.AiAnswerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Component
public class ExternalAiCatalog implements AiCatalog {

    private final RestClient restClient;

    public ExternalAiCatalog(@Qualifier("aiCatalogApi") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override public Optional<AiAnswerDto> askForMove(GameStateDto gameStateDto) {
        log.info("Asking the Ai to make a move");
        try {
            final var response = restClient
                    .post()
                    .uri("/ai-move")
                    .body(gameStateDto)
                    .retrieve()
                    .body(AiAnswerDto.class);

            if (response == null) {
                log.error("No ai response");
            }

            return Optional.ofNullable(response);
        } catch (final HttpStatusCodeException e) {
            log.error("Error while asking AI for a move: {}", e.getMessage());
            return Optional.empty();
        } catch (final ResourceAccessException e) {
            log.error("AI service is unreachable: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
