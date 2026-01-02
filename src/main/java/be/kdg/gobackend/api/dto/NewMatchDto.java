package be.kdg.gobackend.api.dto;

import java.util.UUID;

public record NewMatchDto(int size, UUID player1Id, UUID player2Id) { }