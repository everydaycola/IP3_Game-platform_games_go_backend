package be.kdg.gobackend.domain.repository;


import be.kdg.gobackend.infrastructure.gamestate.ai.dtos.AiRequestBodyDto;
import be.kdg.gobackend.infrastructure.gamestate.ai.dtos.AiAnswerDto;

import java.util.Optional;

public interface AiCatalog {
    Optional<AiAnswerDto> askForMove(AiRequestBodyDto aiRequestBodyDto);
}
