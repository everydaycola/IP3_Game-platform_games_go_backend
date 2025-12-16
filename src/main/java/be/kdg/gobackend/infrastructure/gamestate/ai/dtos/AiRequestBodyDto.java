package be.kdg.gobackend.infrastructure.gamestate.ai.dtos;

public record AiRequestBodyDto(
        String[][] boardState,
        String currentPlayer,
        int iterations
) {
    public static AiRequestBodyDto from(String[][] boardState) {
        return new AiRequestBodyDto(
                boardState,
                "WHITE",
                40
        );
    }
}
