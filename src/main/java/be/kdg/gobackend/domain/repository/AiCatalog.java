package be.kdg.gobackend.domain.repository;


import be.kdg.gobackend.api.dto.GameStateDto;
import be.kdg.gobackend.infrastructure.gamestate.ai.dtos.AiAnswerDto;

import java.util.Optional;

public interface AiCatalog {
    Optional<AiAnswerDto> askForMove(GameStateDto gameStateDto);
}
