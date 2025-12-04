package be.kdg.gobackend.api;

import be.kdg.gobackend.api.dto.GameStateDto;
import be.kdg.gobackend.api.dto.NewMatchDto;
import be.kdg.gobackend.api.dto.NewStoneDto;
import be.kdg.gobackend.application.GameStateService;
import be.kdg.gobackend.domain.game.GameStateId;
import be.kdg.gobackend.domain.player.PlayerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/go/api/matches")
@Slf4j
public class MatchController {

    final GameStateService gameStateService;

    public MatchController(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    @PostMapping()
    public ResponseEntity<GameStateDto> startGame(@AuthenticationPrincipal Jwt token, @RequestBody NewMatchDto newMatchDto){
        log.info("Starting a new game");
        final var playerId = PlayerId.fromToken(token);
        final var state = gameStateService.start(playerId, newMatchDto.size());
        return ResponseEntity.ok(GameStateDto.from(state));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameStateDto> getGame(@PathVariable UUID id){
        log.info("Getting game with id {}", id);
        final var stateId = new GameStateId(id);
        final var state = gameStateService.getState(stateId);
        return ResponseEntity.ok(GameStateDto.from(state));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GameStateDto> updateGame(@PathVariable UUID id, @RequestBody NewStoneDto stone){
        log.info("Player from match {} placing a stone at {}, {}", id, stone.x(), stone.y());
        final var stateId = new GameStateId(id);
        final var state = gameStateService.placeStone(stateId, stone.x(), stone.y());
        return ResponseEntity.ok(GameStateDto.from(state));
    }
}
