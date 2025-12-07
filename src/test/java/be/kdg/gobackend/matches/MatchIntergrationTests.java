package be.kdg.gobackend.matches;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MatchIntergrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class SuccesFlows {
        @Test
        void get_match_should_return_200_with_valid_json_structure() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            // act
            mockMvc.perform(get("/go/api/matches/{id}", gameId)
                            .with(jwt()
                                    .jwt(jwt -> jwt
                                            .subject("10000000-0000-0000-0000-000000000001")
                                            .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                            .claim(StandardClaimNames.FAMILY_NAME, "user")
                                            .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                    )
                            ))
                    // assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("00000000-0000-0000-0000-000000000001"))
                    .andExpect(jsonPath("$.board").isArray())
                    .andExpect(jsonPath("$.board[0]").isArray())
                    .andExpect(jsonPath("$.board[0][0]").value("_"))
                    .andExpect(jsonPath("$.size").isNumber())
                    .andExpect(jsonPath("$.winner").value("EMPTY"))
                    .andExpect(jsonPath("$.score").value(0.0))
                    .andExpect(jsonPath("$.atTurn").value(true))
                    .andExpect(jsonPath("$.isLastTurnPassed").value(false));
        }

        @Test
        void start_new_match_creates_a_match_and_returns_it() throws Exception {
            // act
            mockMvc.perform(post("/go/api/matches")
                            // arrange
                            .contentType("application/json")
                            .content("{\"size\": 9}")
                            .with(jwt()
                                    .jwt(jwt -> jwt
                                            .subject("20000000-0000-0000-0000-000000000001")
                                            .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                            .claim(StandardClaimNames.FAMILY_NAME, "user")
                                            .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                    )
                            ))
                    // assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").isString())
                    .andExpect(jsonPath("$.board").isArray())
                    .andExpect(jsonPath("$.board.length()").value(9))
                    .andExpect(jsonPath("$.board[0]").isArray())
                    .andExpect(jsonPath("$.board[0].length()").value(9))
                    .andExpect(jsonPath("$.board[0][0]").value("_"))
                    .andExpect(jsonPath("$.size").value(9))
                   .andExpect(jsonPath("$.winner").value("EMPTY"))
                   .andExpect(jsonPath("$.score").value(0.0))
                   .andExpect(jsonPath("$.atTurn").value(true))
                   .andExpect(jsonPath("$.isLastTurnPassed").value(false));
        }

        @Test
        void place_stone_should_place_stone_successfully() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000002");
            // act
            mockMvc.perform(patch("/go/api/matches/{id}", gameId)
                            // arrange
                            .contentType("application/json")
                            .content("{\"x\": 1,\"y\": 2}")
                            .with(jwt()
                                    .jwt(jwt -> jwt
                                            .subject("10000000-0000-0000-0000-000000000002")
                                            .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                            .claim(StandardClaimNames.FAMILY_NAME, "user")
                                            .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                    )
                            ))
                    // assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.board[1][2]").value("B"))
                    .andExpect(jsonPath("$.atTurn").value(false))
                    .andExpect(jsonPath("$.isLastTurnPassed").value(false));
        }

        @Test
        void pass_should_make_no_move_and_switch_turn() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000003");
            // act
            mockMvc.perform(patch("/go/api/matches/{id}/pass", gameId)
                            .with(jwt()
                                    .jwt(jwt -> jwt
                                            .subject("10000000-0000-0000-0000-000000000003")
                                            .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                            .claim(StandardClaimNames.FAMILY_NAME, "user")
                                            .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                    )
                            ))
                    // assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.atTurn").value(false))
                    .andExpect(jsonPath("$.isLastTurnPassed").value(true));
        }

        @Test
        void pass_after_pass_should_end_game() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000004");
            // act
            mockMvc.perform(patch("/go/api/matches/{id}/pass", gameId)
                                    .with(jwt()
                                                  .jwt(jwt -> jwt
                                                          .subject("10000000-0000-0000-0000-000000000004")
                                                          .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                                          .claim(StandardClaimNames.FAMILY_NAME, "user")
                                                          .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                                  )
                                    ))
                   // assert
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.winner").value("BLACK"))
                   .andExpect(jsonPath("$.score").value(74.5));
        }
    }

    @Nested
    class BadRequestFlows {
        @Test
        void start_new_match_with_value_under_5_should_give_400() throws Exception {
            // act
            mockMvc.perform(post("/go/api/matches")
                            // arrange
                            .contentType("application/json")
                            .content("{\"size\": 4}")
                            .with(jwt()
                                    .jwt(jwt -> jwt
                                            .subject("0b906b06-53fe-4095-a3bd-32b8aa4e9aba")
                                            .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                            .claim(StandardClaimNames.FAMILY_NAME, "user")
                                            .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                    )
                            ))
                    // assert
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").isString());
        }

        @Test
        void start_new_match_with_value_above_19_should_give_400() throws Exception {
            // act
            mockMvc.perform(post("/go/api/matches")
                            // arrange
                            .contentType("application/json")
                            .content("{\"size\": 20}")
                            .with(jwt()
                                    .jwt(jwt -> jwt
                                            .subject("0b906b06-53fe-4095-a3bd-32b8aa4e9aba")
                                            .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                            .claim(StandardClaimNames.FAMILY_NAME, "user")
                                            .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                    )
                            ))
                    // assert
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").isString());
        }

        @Test
        void place_stone_should_return_400_if_out_bounds() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            // act
            mockMvc.perform(patch("/go/api/matches/{id}", gameId)
                            // arrange
                            .contentType("application/json")
                            .content("{\"x\": 1,\"y\": 99}")
                            .with(jwt()
                                    .jwt(jwt -> jwt
                                            .subject("10000000-0000-0000-0000-000000000001")
                                            .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                            .claim(StandardClaimNames.FAMILY_NAME, "user")
                                            .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                    )
                            ))
                    // assert
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").isString());
        }
    }

    @Nested
    class NotFoundFlows {
        @Test
        void get_match_with_wrong_uuid_return_404_and_message() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            // act
            mockMvc.perform(get("/go/api/matches/{id}", gameId)
                            // assert
                            .with(jwt()
                                    .jwt(jwt -> jwt
                                            .subject("0b906b06-53fe-4095-a3bd-32b8aa4e9aba")
                                            .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                            .claim(StandardClaimNames.FAMILY_NAME, "user")
                                            .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                    )
                            ))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").isString());
        }

        @Test
        void get_match_as_wrong_owner_is_hidden_and_not_found() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            // act
            mockMvc.perform(get("/go/api/matches/{id}", gameId)
                                    .with(jwt()
                                                  .jwt(jwt -> jwt
                                                          .subject("10000000-0000-0000-0000-000000000002")
                                                          .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                                          .claim(StandardClaimNames.FAMILY_NAME, "user")
                                                          .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                                  )
                                    ))
                   // assert
                   .andExpect(status().isNotFound())
                   .andExpect(jsonPath("$.message").isString());
        }
    }

    @Nested
    class SecurityFlows {
        @Test
        void get_match_unautherised_fails() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            // act
            mockMvc.perform(get("/go/api/matches/{id}", gameId))
                    // assert
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void start_new_match_unauthorized_fails() throws Exception {
            // act
            mockMvc.perform(post("/go/api/matches")
                            // arrange
                            .contentType("application/json")
                            .content("{\"size\": 9}"))
                    // assert
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void place_stone_unautherised_fails() throws Exception {
            // arrange
            final var gameId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            // act
            mockMvc.perform(patch("/go/api/matches/{id}", gameId)
                            // arrange
                            .contentType("application/json")
                            .content("{\"x\": 1,\"y\": 2}"))
                    // assert
                    .andExpect(status().isUnauthorized());;
        }
    }
}
