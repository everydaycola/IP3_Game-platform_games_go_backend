package be.kdg.gobackend.domain.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardTest {

    @Nested
    @DisplayName("Success Flows")
    class SuccessFlows {

        @Test
        @DisplayName("Should create a board with the given size")
        void shouldCreateBoard() {
            // Arrange
            final var board = new Board(9);
            final var x = 3;
            final var y = 3;
            final var stone = Stone.BLACK;

            // Act
            board.placeStone(x, y, stone);

            // Assert
            assertThat(board.getStone(x, y)).isEqualTo(stone);
        }

        @Test
        @DisplayName("getBoardForDto() creates the board as a 2d array")
        void getBoardForDtoCreatesBoardAs2dArray(){
            // Arrange
            final var board = new Board(9);
            final var x = 2;
            final var y = 5;
            final var stone = Stone.BLACK;
            board.placeStone(x, y, stone);

            // Act
            final var boardDto = board.getBoardForDto();

            // Assert
            assertThat(boardDto[x][y]).isEqualTo(String.valueOf(stone.getShortName()));
        }

        @Test
        @DisplayName("Should place a BLACK stone on an empty spot")
        void shouldPlaceBlackStone() {
            // Arrange
            final var board = new Board(9);
            final var stone = Stone.BLACK;

            // Act
            board.placeStone(3, 3, stone);

            // Assert
            assertThat(board.getStone(3, 3)).isEqualTo(stone);
        }

        @Test
        @DisplayName("Should place a WHITE stone on an empty spot")
        void shouldPlaceWhiteStone() {
            // Arrange
            final var board = new Board(9);
            final var stone = Stone.WHITE;

            // Act
            board.placeStone(4, 4, stone);

            // Assert
            assertThat(board.getStone(4, 4)).isEqualTo(stone);
        }
    }

    @Nested
    @DisplayName("Error Flows")
    class ErrorFlows {
        @Test
        @DisplayName("Should throw exception when placing stone with X too high")
        void shouldThrowExceptionForOutOfBoundsXToHigh() {
            // Arrange
            final var size = 9;
            final var x = 10;
            final var y = 0;
            final var board = new Board(size);
            final var stone = Stone.BLACK;

            // Act & Assert
            assertThatThrownBy(() -> board.placeStone(x, y, stone))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Position is out of bounds");
        }

        @Test
        @DisplayName("Should throw exception when placing stone with X too low")
        void shouldThrowExceptionForOutOfBoundsXToLow() {
            // Arrange
            final var size = 9;
            final var x = -1;
            final var y = 0;
            final var board = new Board(size);
            final var stone = Stone.BLACK;

            // Act & Assert
            assertThatThrownBy(() -> board.placeStone(x, y, stone))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Position is out of bounds");
        }

        @Test
        @DisplayName("Should throw exception when placing stone with Y too high")
        void shouldThrowExceptionForOutOfBoundsYToHigh() {
            // Arrange
            final var size = 9;
            final var x = 0;
            final var y = 10;
            final var board = new Board(size);
            final var stone = Stone.BLACK;

            // Act & Assert
            assertThatThrownBy(() -> board.placeStone(x, y, stone))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Position is out of bounds");
        }

        @Test
        @DisplayName("Should throw exception when placing stone with Y too low")
        void shouldThrowExceptionForOutOfBoundsYToLow() {
            // Arrange
            final var size = 9;
            final var x = 0;
            final var y = -1;
            final var board = new Board(size);
            final var stone = Stone.BLACK;

            // Act & Assert
            assertThatThrownBy(() -> board.placeStone(x, y, stone))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Position is out of bounds");
        }

        @Test
        @DisplayName("Should throw exception when placing a stone on a non-empty spot")
        void shouldThrowExceptionForOccupiedSpot() {
            // Arrange
            final var size = 9;
            final var x = 5;
            final var y = 5;
            final var board = new Board(size);
            final var initialStone = Stone.BLACK;
            board.placeStone(x, y, initialStone);

            final var newStone = Stone.WHITE;

            // Act & Assert
            assertThatThrownBy(() -> board.placeStone(x, y, newStone))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Spot is already taken");
        }

        @Test
        @DisplayName("Should not create board when size is under 5")
        void shouldNotCreateBoardWhenSizeUnder5() {
            // Arrange
            final var size = 4;

            // Act & Assert
            assertThatThrownBy(() -> new Board(size))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Board size must be between 5 and 19");
        }

        @Test
        @DisplayName("Should not create board when size is above 19")
        void shouldNotCreateBoardWhenSizeOver19() {
            // Arrange
            final var size = 20;

            // Act & Assert
            assertThatThrownBy(() -> new Board(size))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Board size must be between 5 and 19");
        }
    }
}