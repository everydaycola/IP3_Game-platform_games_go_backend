package be.kdg.gobackend.infrastructure.gamestate.ai.dtos;

import be.kdg.gobackend.domain.game.GameState;

public record AiRequestBodyDto(
        String[][] boardState,
        String currentPlayer,
        boolean isLastTurnPassed

) {
    public static AiRequestBodyDto from(GameState gameState) {
        return new AiRequestBodyDto(
                gameState.getBoardForDto(),
                "WHITE",
                gameState.isLastTurnPassed()
        );
    }
}
