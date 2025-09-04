package com.gitmerge.controller;

import com.gitmerge.entity.Match;
import com.gitmerge.entity.Game;
import com.gitmerge.entity.User;
import com.gitmerge.service.MatchService;
import com.gitmerge.service.GameService;
import com.gitmerge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {
    private final MatchService matchService;
    private final GameService gameService;
    private final UserService userService;
    
    @PostMapping("/queue")
    public ResponseEntity<Match> queueForMatch(@RequestParam Long gameId, 
                                              @RequestParam String nickname) {
        Game game = gameService.findBySeed(gameId.toString())
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        User user = userService.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Match match = matchService.createOrJoinMatch(game, user);
        return ResponseEntity.ok(match);
    }
    
    @PutMapping("/{matchId}/finish")
    public ResponseEntity<Void> finishMatch(@PathVariable Long matchId) {
        matchService.finishMatch(matchId);
        return ResponseEntity.ok().build();
    }
}
