package be.kdg.gobackend.api.dto;

import be.kdg.gobackend.domain.game.GameState;

import java.util.UUID;

public record GameStateDto(
        UUID id,
        String[][] board,
        int size
) {
    public static GameStateDto from(GameState gameState) {
        return new GameStateDto(
                gameState.getId().id(),
                gameState.getBoard().getBoardForDto(),
                gameState.getBoard().getSize()
        );
    }
}
