package be.kdg.gobackend.domain.player;

import be.kdg.gobackend.domain.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@Slf4j
public record PlayerId(UUID id) {

    public NotFoundException notFound() {
        log.error("User with id {} not found", id);
        return new NotFoundException("User [" + id + "] not found");
    }

    public static PlayerId fromToken(Jwt token) {
        return new PlayerId(UUID.fromString(token.getClaimAsString("sub")));
    }
}
