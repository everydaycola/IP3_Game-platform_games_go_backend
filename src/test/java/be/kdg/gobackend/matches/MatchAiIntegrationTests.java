package be.kdg.gobackend.matches;

import be.kdg.gobackend.infrastructure.gamestate.ai.dtos.AiAnswerDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class MatchAiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    // THIS IS THE KEY: Replaces the real Bean with a Mockito mock
    @MockBean
    private AiApiService aiApiService;

    @Test
    void shouldRequestAiMoveAndSaveToDb() throws Exception {
        // Arrange
        final var mockedMove = new AiAnswerDto(50, 5, 5);
        Mockito.when(aiApiService.getNextMove()).thenReturn(mockedMove);

        // Act
        mockMvc.perform(post("/api/game/1/ai-move")
                                .contentType(MediaType.APPLICATION_JSON))
               // Assert
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.lastMove.x").value(3))
               .andExpect(jsonPath("$.lastMove.y").value(15));

        Mockito.verify(aiApiService).getNextMove();
    }
}
