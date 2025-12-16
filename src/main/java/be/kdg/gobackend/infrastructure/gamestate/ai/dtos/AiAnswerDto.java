package be.kdg.gobackend.infrastructure.gamestate.ai.dtos;

public record AiAnswerDto(
        int best_move,
        int row,
        int col
) {
}
