package be.kdg.gobackend.infrastructure.gamestate.analytics.messages;

import be.kdg.gobackend.domain.game.GameState;
import be.kdg.gobackend.domain.game.Stone;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record WinnerDeclaredMessage(
        String event_type,
        String timestamp,
        UUID game_id,
        UUID session_id,
        String winner,
        UUID winner_id,
        String game_name
) implements EventMessage {
    public static WinnerDeclaredMessage of(GameState gameState) {
        return new WinnerDeclaredMessage(
                "winner_declared",
                LocalDateTime.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT),
                gameState.getId().id(),
                gameState.getId().id(),
                (gameState.isAiGame() && gameState.getWinner() == Stone.WHITE) ? "AI" : "Human",
                (gameState.getWinner() == Stone.WHITE) ? gameState.getPlayer1().id() : gameState.getPlayer2().id(),
                "go"
        );
    }
}
