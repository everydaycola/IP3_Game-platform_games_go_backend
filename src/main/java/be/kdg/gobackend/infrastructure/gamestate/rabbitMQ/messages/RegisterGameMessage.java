package be.kdg.gobackend.infrastructure.gamestate.rabbitMQ.messages;

import be.kdg.gobackend.api.dto.registeration.FullGameDto;

public record RegisterGameMessage(FullGameDto gameDto) {
}
