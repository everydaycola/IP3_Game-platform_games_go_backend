package be.kdg.gobackend.domain.game;

import be.kdg.gobackend.domain.exception.NotFoundException;
import be.kdg.gobackend.domain.player.PlayerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GameStateTest {

    @DisplayName("Success Flows")
    @Nested
    class SuccessFlows {
        @Test
        @DisplayName("Should successfully place black stone on an empty board")
        void shouldPlaceBlackStoneOnEmptyBoard() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, true, false, 0.0, null);

            final var x = 3;
            final var y = 5;
            final var stone = Stone.BLACK;

            // Act
            gameState.placeStone(x, y, stone);

            // Assert
            verify(mockBoard).placeStone(x, y, stone);
            assertThat(gameState.isPlayer1AtTurn()).isFalse();
            assertThat(gameState.isLastTurnPassed()).isFalse();
        }

        @Test
        @DisplayName("Should successfully place white stone")
        void shouldPlaceWhiteStoneOnBoard() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, false, false, 0.0, null);

            final var x = 2;
            final var y = 2;
            final var stone = Stone.WHITE;

            // Act
            gameState.placeStone(x, y, stone);

            // Assert
            verify(mockBoard).placeStone(x, y, stone);
            assertThat(gameState.isPlayer1AtTurn()).isTrue();
        }

        @Test
        @DisplayName("Pass turn should switch turn and set flag")
        void passTurnShouldSwitch() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, true, false, 0.0, null);

            // Act
            gameState.passTurn(Stone.BLACK);

            // Assert
            assertThat(gameState.isPlayer1AtTurn()).isFalse();
            assertThat(gameState.isLastTurnPassed()).isTrue();
        }

        @Test
        @DisplayName("Double pass should end game and calculate score (Black Wins)")
        void doublePassEndsGameBlackWins() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, false, true, 0.0, null);

            when(mockBoard.calculateScore()).thenReturn(10.0);

            // Act
            gameState.passTurn(Stone.WHITE);

            // Assert
            assertThat(gameState.getScore()).isEqualTo(3.5);
            assertThat(gameState.getWinner()).isEqualTo(Stone.BLACK);
        }

        @Test
        @DisplayName("Double pass should end game and calculate score (White Wins)")
        void doublePassEndsGameWhiteWins() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, false, true, 0.0, null);

            when(mockBoard.calculateScore()).thenReturn(6.0);

            // Act
            gameState.passTurn(Stone.WHITE);

            // Assert
            assertThat(gameState.getScore()).isEqualTo(-0.5);
            assertThat(gameState.getWinner()).isEqualTo(Stone.WHITE);
        }

        @Test
        @DisplayName("Verify player should succeed if IDs match")
        void verifyPlayerSuccess() {
            // Arrange
            final var uuid = UUID.randomUUID();
            final var playerId = new PlayerId(uuid);
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(9, playerId,aiPlayerId,true);

            // Act & Assert (Should not throw error)
            gameState.verifyPlayer(new PlayerId(uuid));
        }

        @Test
        @DisplayName("Constructors and Getters work correctly")
        void constructorsAndGetters() {
            // Arrange
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(13, playerId,aiPlayerId,true);

            // Act & Assert
            assertThat(gameState.getSize()).isEqualTo(13);
            assertThat(gameState.getPlayer1()).isEqualTo(playerId);
            assertThat(gameState.getBoard()).isNotNull();
            assertThat(gameState.getId()).isNotNull();
            assertThat(gameState.isPlayer1AtTurn()).isTrue();
            assertThat(gameState.getWinner()).isEqualTo(Stone.EMPTY);
        }

        @Test
        @DisplayName("getBoardForDto delegates to board")
        void getBoardForDto() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, true, false, 0.0, null);
            final String[][] expected = new String[0][0];

            when(mockBoard.getBoardForDto()).thenReturn(expected);

            // Act
            final var res = gameState.getBoardForDto();

            // Assert
            assertThat(res).isSameAs(expected);
        }
    }

    @Nested
    @DisplayName("Error Flows")
    class ErrorFlows{
        @Test
        @DisplayName("Should fail to place black stone if it's ai turn (White)")
        void shouldFailToPlaceBlackStone() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, false, false, 0.0, null);
            final var stone = Stone.BLACK;

            // Act & Assert
            assertThatThrownBy(() -> gameState.placeStone(3, 5, stone))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("It is not player's turn");

            verify(mockBoard, never()).placeStone(anyInt(), anyInt(), any());
        }

        @Test
        @DisplayName("Should fail to place white stone if it's player turn (Black)")
        void shouldFailToPlaceWhiteStone() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, true, false, 0.0, null);
            final var stone = Stone.WHITE;

            // Act & Assert
            assertThatThrownBy(() -> gameState.placeStone(2, 2, stone))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("It is not player's turn");
        }

        @Test
        @DisplayName("Passing turn out of order throws exception")
        void passTurnOutOfOrder() {
            // Arrange
            final var mockBoard = Mockito.mock(Board.class);
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(new GameStateId(), mockBoard,true, playerId,aiPlayerId, true, false, 0.0, null);

            // Act & Assert
            assertThatThrownBy(() -> gameState.passTurn(Stone.WHITE))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("It is not player's turn");
        }

        @Test
        @DisplayName("Verify player throws NotFound if IDs do not match")
        void verifyPlayerFail() {
            // Arrange
            final var playerId = new PlayerId(UUID.randomUUID());
            final var aiPlayerId = new PlayerId(UUID.randomUUID());
            final var gameState = new GameState(9, playerId,aiPlayerId, true);
            final var otherPlayer = new PlayerId(UUID.randomUUID());

            // Act & Assert
            assertThatThrownBy(() -> gameState.verifyPlayer(otherPlayer))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("Game was not found");
        }
    }
}