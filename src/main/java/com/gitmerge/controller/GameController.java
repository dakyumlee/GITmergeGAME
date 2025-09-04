package com.gitmerge.controller;

import com.gitmerge.enums.Difficulty;
import com.gitmerge.service.GameLogicService;
import com.gitmerge.service.GameLogicService.*;
import com.gitmerge.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

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
            
            StartGameResponse response = new StartGameResponse();
            response.setSessionId(session.getSessionId());
            response.setDifficulty(session.getDifficulty());
            response.setTimeLimit(session.getTimeLimit());
            response.setTotalConflicts(session.getConflictData().getTotalConflicts());
            response.setConflicts(session.getConflictData().getConflicts());
            
            return ResponseEntity.ok(response);
            
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
            
            ResolveConflictResponse response = new ResolveConflictResponse();
            response.setCorrect(result.isCorrect());
            response.setAccuracy(result.getAccuracy());
            response.setCleanliness(result.getCleanliness());
            response.setGameCompleted(result.isGameCompleted());
            response.setFileName(request.getFileName());
            
            return ResponseEntity.ok(response);
            
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
            
            FinishGameResponse response = new FinishGameResponse();
            response.setSessionId(result.getSessionId());
            response.setTotalScore(result.getTotalScore());
            response.setCorrectCount(result.getCorrectCount());
            response.setTotalConflicts(result.getTotalConflicts());
            response.setAccuracy(result.getAccuracy());
            response.setCleanliness(result.getCleanliness());
            response.setElapsedTime(result.getElapsedTime());
            response.setStatus(result.getStatus().toString());
            response.setDifficulty(result.getDifficulty());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/finish-with-achievements/{sessionId}")
    public ResponseEntity<?> finishGameWithAchievements(@PathVariable String sessionId) {
        try {
            GameResultWithAchievements result = gameLogicService.finishGameWithAchievements(sessionId);
            
            if (result == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Session not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                "gameResult", convertToMap(result.getGameResult()),
                "newAchievements", result.getNewAchievements(),
                "hasNewAchievements", result.hasNewAchievements()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private Map<String, Object> convertToMap(GameResult result) {
        return Map.of(
            "sessionId", result.getSessionId(),
            "totalScore", result.getTotalScore(),
            "correctCount", result.getCorrectCount(),
            "totalConflicts", result.getTotalConflicts(),
            "accuracy", result.getAccuracy(),
            "cleanliness", result.getCleanliness(),
            "elapsedTime", result.getElapsedTime(),
            "status", result.getStatus().toString(),
            "difficulty", result.getDifficulty().toString()
        );
    }
    
    public static class StartGameRequest {
        private Long userId;
        private Difficulty difficulty;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Difficulty getDifficulty() { return difficulty; }
        public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    }
    
    public static class StartGameResponse {
        private String sessionId;
        private Difficulty difficulty;
        private int timeLimit;
        private int totalConflicts;
        private Object conflicts;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Difficulty getDifficulty() { return difficulty; }
        public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
        public int getTimeLimit() { return timeLimit; }
        public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
        public int getTotalConflicts() { return totalConflicts; }
        public void setTotalConflicts(int totalConflicts) { this.totalConflicts = totalConflicts; }
        public Object getConflicts() { return conflicts; }
        public void setConflicts(Object conflicts) { this.conflicts = conflicts; }
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
    
    public static class ResolveConflictResponse {
        private String fileName;
        private boolean correct;
        private int accuracy;
        private int cleanliness;
        private boolean gameCompleted;
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
        public int getAccuracy() { return accuracy; }
        public void setAccuracy(int accuracy) { this.accuracy = accuracy; }
        public int getCleanliness() { return cleanliness; }
        public void setCleanliness(int cleanliness) { this.cleanliness = cleanliness; }
        public boolean isGameCompleted() { return gameCompleted; }
        public void setGameCompleted(boolean gameCompleted) { this.gameCompleted = gameCompleted; }
    }
    
    public static class FinishGameResponse {
        private String sessionId;
        private int totalScore;
        private int correctCount;
        private int totalConflicts;
        private int accuracy;
        private int cleanliness;
        private long elapsedTime;
        private String status;
        private Difficulty difficulty;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public int getTotalScore() { return totalScore; }
        public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
        public int getCorrectCount() { return correctCount; }
        public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }
        public int getTotalConflicts() { return totalConflicts; }
        public void setTotalConflicts(int totalConflicts) { this.totalConflicts = totalConflicts; }
        public int getAccuracy() { return accuracy; }
        public void setAccuracy(int accuracy) { this.accuracy = accuracy; }
        public int getCleanliness() { return cleanliness; }
        public void setCleanliness(int cleanliness) { this.cleanliness = cleanliness; }
        public long getElapsedTime() { return elapsedTime; }
        public void setElapsedTime(long elapsedTime) { this.elapsedTime = elapsedTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Difficulty getDifficulty() { return difficulty; }
        public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    }
}
