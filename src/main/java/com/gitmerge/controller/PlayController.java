package com.gitmerge.controller;

import com.gitmerge.entity.*;
import com.gitmerge.enums.ResultStatus;
import com.gitmerge.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/plays")
@RequiredArgsConstructor
public class PlayController {
    private final PlayService playService;
    private final UserService userService;
    private final GameService gameService;
    private final MatchService matchService;
    private final ReplayService replayService;
    
    @PostMapping("/{gameId}/start")
    public ResponseEntity<Map<String, Object>> startGame(@PathVariable Long gameId, 
                                                        @RequestParam String nickname) {
        User user = userService.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return ResponseEntity.ok(Map.of(
            "message", "Game started",
            "userId", user.getId(),
            "gameId", gameId
        ));
    }
    
    @PostMapping("/{gameId}/submit")
    public ResponseEntity<Map<String, Object>> submitResult(@PathVariable Long gameId,
                                                           @RequestParam String nickname,
                                                           @RequestParam Long timeTakenMs,
                                                           @RequestParam Boolean buildPassed,
                                                           @RequestParam BigDecimal accuracy,
                                                           @RequestParam BigDecimal cleanliness,
                                                           @RequestParam Integer retries,
                                                           @RequestParam ResultStatus status,
                                                           @RequestParam(required = false) String actionLog) {
        
        User user = userService.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Game game = gameService.findBySeed(gameId.toString())
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        
        Result result = playService.submitResult(user, game, null, timeTakenMs, 
                                                buildPassed, accuracy, cleanliness, retries, status);
        
        if (actionLog != null) {
            replayService.saveReplay(result, actionLog);
        }
        
        return ResponseEntity.ok(Map.of(
            "score", result.getScore(),
            "resultId", result.getId(),
            "message", "Result submitted successfully"
        ));
    }
}
