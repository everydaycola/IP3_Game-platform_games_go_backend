package be.kdg.gobackend.api.dto;

import java.util.UUID;

//boardSize here must match the go.json's since its send in from UI.
public record NewMatchDto(UUID player1Id, UUID player2Id, GameSettingsDto settings) { }
