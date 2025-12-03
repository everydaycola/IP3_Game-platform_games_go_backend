package be.kdg.gobackend.domain.game;

import be.kdg.gobackend.domain.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public record GameStateId(UUID id) {
    public GameStateId() {
        this(UUID.randomUUID());
    }

    public NotFoundException notFound() {
        log.error("Match with id {} not found", id);
        return new NotFoundException("Match [" + id + "] not found");
    }
}
