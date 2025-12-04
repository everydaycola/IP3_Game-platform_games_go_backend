package be.kdg.gobackend.matches;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
            UUID gameId = UUID.fromString("0b83d863-cbfb-4138-a210-9ea6df7653dc");
            // act
            mockMvc.perform(get("/go/api/matches/{id}", gameId)
                    // assert
                                    .with(jwt()
                                                  .jwt(jwt -> jwt
                                                          .subject(UUID.randomUUID().toString())
                                                          .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                                          .claim(StandardClaimNames.FAMILY_NAME, "user")
                                                          .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                                  )
                                    ))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.id").value("0b83d863-cbfb-4138-a210-9ea6df7653dc"))
                   .andExpect(jsonPath("$.board").isArray())
                   .andExpect(jsonPath("$.board[0]").isArray())
                   .andExpect(jsonPath("$.board[0][0]").value("_"))
                   .andExpect(jsonPath("$.size").isNumber());
        }

        @Test
        void start_new_match_creates_a_match_and_returns_it() throws Exception {
            // act
            mockMvc.perform(post("/go/api/matches")
                                    // arrange
                                    .contentType("application/json")
                                    .content("{\"size\": 9}")
                                    // assert
                                    .with(jwt()
                                                  .jwt(jwt -> jwt
                                                          .subject(UUID.randomUUID().toString())
                                                          .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                                          .claim(StandardClaimNames.FAMILY_NAME, "user")
                                                          .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                                  )
                                    ))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.id").isString())
                   .andExpect(jsonPath("$.board").isArray())
                   .andExpect(jsonPath("$.board.length()").value(9))
                   .andExpect(jsonPath("$.board[0]").isArray())
                   .andExpect(jsonPath("$.board[0].length()").value(9))
                   .andExpect(jsonPath("$.board[0][0]").value("_"))
                   .andExpect(jsonPath("$.size").value(9));
        }
    }

    @Nested
    class ErrorFlows{
        @Test
        void get_match_with_wrong_uuid_return_404_and_message() throws Exception {
            // arrange
            UUID gameId = UUID.fromString("00000000-0000-0000-0000-000000000000");
            // act
            mockMvc.perform(get("/go/api/matches/{id}", gameId)
                    // assert
                                    .with(jwt()
                                                  .jwt(jwt -> jwt
                                                          .subject(UUID.randomUUID().toString())
                                                          .claim(StandardClaimNames.GIVEN_NAME, "test_user")
                                                          .claim(StandardClaimNames.FAMILY_NAME, "user")
                                                          .claim(StandardClaimNames.EMAIL, "test_user@test.be")
                                                  )
                                    ))
                   .andExpect(status().isNotFound())
                   .andExpect(jsonPath("$.message").isString());
        }
    }

    @Nested
    class SecurityFlows{
        @Test
        void get_match_should_return_200_with_valid_json_structure() throws Exception {
            // arrange
            UUID gameId = UUID.fromString("0b83d863-cbfb-4138-a210-9ea6df7653dc");
            // act
            mockMvc.perform(get("/go/api/matches/{id}", gameId))
                    // assert
                   .andExpect(status().isUnauthorized());
        }

        @Test
        void start_new_match_creates_a_match_and_returns_it() throws Exception {
            // act
            mockMvc.perform(post("/go/api/matches")
                    // arrange
                                    .contentType("application/json")
                                    .content("{\"size\": 9}"))
                   // assert
                   .andExpect(status().isUnauthorized());
        }
    }
}
