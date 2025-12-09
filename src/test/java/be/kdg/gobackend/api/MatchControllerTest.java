package be.kdg.gobackend.api;

import be.kdg.gobackend.TestHelpers;
import be.kdg.gobackend.api.dto.GameStateDto;
import be.kdg.gobackend.application.GameStateService;
import be.kdg.gobackend.domain.game.GameState;
import be.kdg.gobackend.domain.game.Stone;
import be.kdg.gobackend.domain.player.PlayerId;
import be.kdg.gobackend.domain.repository.AiCatalog;
import be.kdg.gobackend.infrastructure.gamestate.DbGameStateRepository;
import be.kdg.gobackend.infrastructure.gamestate.ai.dtos.AiAnswerDto;
import be.kdg.gobackend.infrastructure.gamestate.jpa.JpaGameStateEntity;
import be.kdg.gobackend.infrastructure.gamestate.jpa.JpaGameStateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@WebMvcTest(MatchController.class)
@Import({GameStateService.class, DbGameStateRepository.class})
class MatchControllerTest {
    @Autowired
    private MatchController sut;

    @MockitoBean
    private JpaGameStateRepository jpaGameStateRepository;

    @MockitoBean
    private AiCatalog aiCatalog;

    @Nested
    @DisplayName("Success Flows")
    class SuccessFlows {
        @Test
        @DisplayName("Should successfully let the Ai make a new move")
        void aiMakesAMove() {
            // Arrange
            UUID playerUUID = UUID.randomUUID();
            final var playerId = new PlayerId(playerUUID);
            final var match = new GameState(9, playerId);
            match.placeStone(0,8, Stone.BLACK);
            final var jpaEntity = JpaGameStateEntity.fromDomain(match);

            when(jpaGameStateRepository.findById(match.getId().id()))
                    .thenReturn(Optional.of(jpaEntity));
            final var aiAnswerDto = new AiAnswerDto(50, 5, 6, false);
            when(aiCatalog.askForMove(Mockito.any(GameStateDto.class)))
                    .thenReturn(Optional.of(aiAnswerDto));

            final var jwtToken = Jwt.withTokenValue("token")
                                     .header("alg", "none")
                                     .subject(playerUUID.toString())
                                     .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                     .claim(StandardClaimNames.FAMILY_NAME, "user")
                                     .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                     .build();

            // Act
            final var result = sut.letAiPlaceStone(jwtToken, match.getId().id());

            // Assert
            Assertions.assertNotNull(result.getBody());
            Assertions.assertEquals(match.getId().id(), result.getBody().id());
            Assertions.assertEquals(9, result.getBody().size());
            Assertions.assertEquals("W", result.getBody().board()[5][6]);
            TestHelpers.assertBoardCounts(result.getBody().board(), 79, 1, 1);

            Mockito.verify(jpaGameStateRepository).findById(Mockito.any(UUID.class));
            Mockito.verify(jpaGameStateRepository).save(Mockito.any(JpaGameStateEntity.class));
            Mockito.verify(aiCatalog).askForMove(Mockito.any(GameStateDto.class));
        }

        @Test
        @DisplayName("Should should pass")
        void aiPasses() {
            // Arrange
            UUID playerUUID = UUID.randomUUID();
            final var playerId = new PlayerId(playerUUID);
            final var match = new GameState(9, playerId);
            match.passTurn(Stone.BLACK);
            final var jpaEntity = JpaGameStateEntity.fromDomain(match);

            when(jpaGameStateRepository.findById(match.getId().id()))
                    .thenReturn(Optional.of(jpaEntity));
            when(aiCatalog.askForMove(Mockito.any(GameStateDto.class)))
                    .thenReturn(Optional.of(new AiAnswerDto(0, 0, 0, true)));

            final var jwtToken = Jwt.withTokenValue("token")
                                     .header("alg", "none")
                                     .subject(playerUUID.toString())
                                     .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                     .claim(StandardClaimNames.FAMILY_NAME, "user")
                                     .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                     .build();

            // Act
            final var result = sut.letAiPlaceStone(jwtToken, match.getId().id());

            // Assert
            Assertions.assertNotNull(result.getBody());
            Assertions.assertEquals(match.getId().id(), result.getBody().id());
            Assertions.assertEquals(9, result.getBody().size());
            TestHelpers.assertBoardCounts(result.getBody().board(), 81, 0, 0);

            Mockito.verify(jpaGameStateRepository).findById(Mockito.any(UUID.class));
            Mockito.verify(jpaGameStateRepository).save(Mockito.any(JpaGameStateEntity.class));
            Mockito.verify(aiCatalog).askForMove(Mockito.any(GameStateDto.class));
        }
    }

    @Nested
    class ErrorFlows {
        @Test
        @DisplayName("Should fail when the ai does not make a move")
        void aiMakesEmptyMove() {
            // Arrange
            UUID playerUUID = UUID.randomUUID();
            final var playerId = new PlayerId(playerUUID);
            final var match = new GameState(9, playerId);
            match.placeStone(0,8, Stone.BLACK);
            final var jpaEntity = JpaGameStateEntity.fromDomain(match);

            when(jpaGameStateRepository.findById(match.getId().id()))
                    .thenReturn(Optional.of(jpaEntity));
            when(aiCatalog.askForMove(Mockito.any(GameStateDto.class)))
                    .thenReturn(Optional.empty());

            final var jwtToken = Jwt.withTokenValue("token")
                                     .header("alg", "none")
                                     .subject(playerUUID.toString())
                                     .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                     .claim(StandardClaimNames.FAMILY_NAME, "user")
                                     .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                     .build();

            // Act & Assert
            assertThatThrownBy(() -> sut.letAiPlaceStone(jwtToken, match.getId().id()))
                    .isInstanceOf(IllegalStateException.class);

            Mockito.verify(jpaGameStateRepository).findById(Mockito.any(UUID.class));
            Mockito.verify(jpaGameStateRepository, never()).save(Mockito.any(JpaGameStateEntity.class));
            Mockito.verify(aiCatalog).askForMove(Mockito.any(GameStateDto.class));
        }
    }
}