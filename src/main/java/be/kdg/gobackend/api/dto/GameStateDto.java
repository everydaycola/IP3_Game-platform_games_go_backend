package be.kdg.gobackend.api.dto;

import be.kdg.gobackend.domain.game.GameState;

import java.util.UUID;

public record GameStateDto(
        UUID id,
        String[][] board,
        int size,
        String winner,
        double score,
        UUID player1Id,
        UUID player2Id,
        boolean isPlayer1AtTurn,
        boolean isLastTurnPassed,
        boolean isAiGame
) {
    public static GameStateDto from(GameState gameState) {
        return new GameStateDto(
                gameState.getId().id(),
                gameState.getBoardForDto(),
                gameState.getSize(),
                gameState.getWinner().toString(),
                gameState.getScore(),
                gameState.getPlayer1().id(),
                gameState.getPlayer2().id(),
                gameState.isPlayer1AtTurn(),
                gameState.isLastTurnPassed(),
                gameState.isAiGame()
        );
    }
}
