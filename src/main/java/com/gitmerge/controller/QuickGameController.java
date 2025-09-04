package com.gitmerge.controller;

import com.gitmerge.enums.Difficulty;
import com.gitmerge.service.QuickGameService;
import com.gitmerge.service.QuickGameService.SubmitResult;
import com.gitmerge.service.QuickGameService.GameResult;
import com.gitmerge.service.QuickGameService.QuickGameSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/quick")
@CrossOrigin(origins = "*")
public class QuickGameController {
    
    @Autowired
    private QuickGameService quickGameService;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startQuickGame(@RequestBody Map<String, Object> request) {
        String nickname = (String) request.get("nickname");
        String difficultyStr = (String) request.get("difficulty");
        Difficulty difficulty = Difficulty.valueOf(difficultyStr);
        
        QuickGameService.QuickGameSession session = quickGameService.startQuickGame(nickname, difficulty);
        
        return ResponseEntity.ok(Map.of(
            "sessionId", session.sessionId,
            "currentConflict", session.conflicts.get(0),
            "totalConflicts", session.conflicts.size(),
            "currentIndex", 0
        ));
    }
    
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> request) {
        String sessionId = (String) request.get("sessionId");
        String answer = (String) request.get("answer");
        
        SubmitResult result = quickGameService.submitAnswer(sessionId, answer);
        
        return ResponseEntity.ok(Map.of(
            "correct", result.correct,
            "message", result.message,
            "gameOver", result.gameOver
        ));
    }
    
    @GetMapping("/result/{sessionId}")
    public ResponseEntity<GameResult> getGameResult(@PathVariable String sessionId) {
        GameResult result = quickGameService.getGameResult(sessionId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
