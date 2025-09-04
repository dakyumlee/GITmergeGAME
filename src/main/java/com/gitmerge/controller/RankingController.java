package com.gitmerge.controller;

import com.gitmerge.entity.SeasonRank;
import com.gitmerge.entity.Result;
import com.gitmerge.service.SeasonService;
import com.gitmerge.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {
    private final SeasonService seasonService;
    private final ResultRepository resultRepository;
    
    @GetMapping("/global")
    public ResponseEntity<List<Result>> getGlobalRanking(@RequestParam(defaultValue = "100") int limit) {
        List<Result> topResults = resultRepository.findAll().stream()
                .sorted((r1, r2) -> Integer.compare(r2.getScore(), r1.getScore()))
                .limit(limit)
                .toList();
        return ResponseEntity.ok(topResults);
    }
    
    @GetMapping("/season")
    public ResponseEntity<List<SeasonRank>> getCurrentSeasonRanking() {
        List<SeasonRank> rankings = seasonService.getCurrentSeasonRanking();
        return ResponseEntity.ok(rankings);
    }
    
    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Result>> getGameLeaderboard(@PathVariable Long gameId) {
        List<Result> leaderboard = resultRepository.findGameLeaderboard(gameId);
        return ResponseEntity.ok(leaderboard);
    }
}
