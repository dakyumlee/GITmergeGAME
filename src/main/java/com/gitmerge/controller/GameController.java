package com.gitmerge.controller;

import com.gitmerge.enums.Difficulty;
import com.gitmerge.service.GameLogicService;
import com.gitmerge.service.GameLogicService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {
    
    @Autowired
    private GameLogicService gameLogicService;
    
    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestBody StartGameRequest request) {
        try {
            GameSession session = gameLogicService.startGame(request.getUserId(), request.getDifficulty());
            
            return ResponseEntity.ok(Map.of(
                "sessionId", session.getSessionId(),
                "difficulty", session.getDifficulty().toString(),
                "timeLimit", session.getTimeLimit(),
                "totalConflicts", session.getConflictData().getTotalConflicts(),
                "conflicts", session.getConflictData().getConflicts()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/resolve")
    public ResponseEntity<?> submitResolution(@RequestBody ResolveConflictRequest request) {
        try {
            ConflictResolutionResult result = gameLogicService.submitResolution(
                request.getSessionId(), 
                request.getFileName(), 
                request.getResolution()
            );
            
            if (!result.isSuccess()) {
                return ResponseEntity.badRequest().body(Map.of("error", result.getErrorMessage()));
            }
            
            return ResponseEntity.ok(Map.of(
                "fileName", request.getFileName(),
                "correct", result.isCorrect(),
                "accuracy", result.getAccuracy(),
                "cleanliness", result.getCleanliness(),
                "gameCompleted", result.isGameCompleted()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/finish/{sessionId}")
    public ResponseEntity<?> finishGame(@PathVariable String sessionId) {
        try {
            GameResult result = gameLogicService.finishGame(sessionId);
            
            if (result == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Session not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                "sessionId", result.getSessionId(),
                "totalScore", result.getTotalScore(),
                "correctCount", result.getCorrectCount(),
                "totalConflicts", result.getTotalConflicts(),
                "accuracy", result.getAccuracy(),
                "cleanliness", result.getCleanliness(),
                "elapsedTime", result.getElapsedTime(),
                "status", result.getStatus().toString(),
                "difficulty", result.getDifficulty().toString()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    public static class StartGameRequest {
        private Long userId;
        private Difficulty difficulty;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Difficulty getDifficulty() { return difficulty; }
        public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    }
    
    public static class ResolveConflictRequest {
        private String sessionId;
        private String fileName;
        private String resolution;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
    }
}
