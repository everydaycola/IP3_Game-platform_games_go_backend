package be.kdg.gobackend.domain.game;

import be.kdg.gobackend.domain.player.PlayerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GameStateTest {
    @DisplayName("SuccessFlows")
    @Nested
    class SuccessFlows {
        @Test
        @DisplayName("Should successfully place black stone on an empty board")
        void shouldPlaceBlackStoneOnEmptyBoard() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard, playerId, true, false, 0.0, null);

            final var x = 3;
            final var y = 5;
            final var stone = Stone.BLACK;

            // Act
            gameState.placeStone(x, y, stone);

            // Assert
            verify(mockBoard).placeStone(x, y, stone);
        }

        @Test
        @DisplayName("Should successfully place white stone without replacing existing stone")
        void shouldPlaceWhiteStoneOnBoard() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard, playerId, false, false, 0.0, null);

            final var x = 2;
            final var y = 2;
            final var stone = Stone.WHITE;

            // Act
            gameState.placeStone(x, y, stone);

            // Assert
            verify(mockBoard).placeStone(x, y, stone);
        }
    }

    @Nested
    class ErrorFlows{
        @Test
        @DisplayName("Should fail to place black stone if it's ai turn")
        void shouldFailToPlaceBlackStone() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard, playerId, false, false, 0.0, null);

            final var x = 3;
            final var y = 5;
            final var stone = Stone.BLACK;

            // Act & Assert
            assertThatThrownBy(() -> gameState.placeStone(x, y, stone))
                    .isInstanceOf(IllegalStateException.class);

            verify(mockBoard, never()).placeStone(x, y, stone);
        }

        @Test
        @DisplayName("Should fail to place white stone if it's player turn")
        void shouldFailToPlaceWhiteStone() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard, playerId, true, false, 0.0, null);

            final var x = 2;
            final var y = 2;
            final var stone = Stone.WHITE;

            // Act & Assert
            assertThatThrownBy(() -> gameState.placeStone(x, y, stone))
                    .isInstanceOf(IllegalStateException.class);

            verify(mockBoard, never()).placeStone(x, y, stone);
        }
    }
}