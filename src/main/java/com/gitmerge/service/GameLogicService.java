package com.gitmerge.service;

import com.gitmerge.enums.Difficulty;
import com.gitmerge.service.ConflictGeneratorService.ConflictData;
import com.gitmerge.service.ConflictGeneratorService.ConflictBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameLogicService {
    
    @Autowired
    private ConflictGeneratorService conflictGenerator;
    
    @Autowired
    private AchievementService achievementService;
    
    private static final int BASE_SCORE = 1000;
    private static final Map<Difficulty, Integer> TIME_LIMITS = Map.of(
        Difficulty.EASY, 300,
        Difficulty.NORMAL, 480,  
        Difficulty.HARD, 600,
        Difficulty.HELL, 900
    );
    
    private Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    public GameSession startGame(Long userId, Difficulty difficulty) {
        ConflictData conflictData = conflictGenerator.generateConflictByDifficulty(difficulty);
        
        GameSession session = new GameSession();
        session.setSessionId(conflictData.getSessionId());
        session.setUserId(userId);
        session.setDifficulty(difficulty);
        session.setConflictData(conflictData);
        session.setStartTime(LocalDateTime.now());
        session.setTimeLimit(TIME_LIMITS.get(difficulty));
        session.setStatus(GameStatus.IN_PROGRESS);
        
        sessions.put(session.getSessionId(), session);
        return session;
    }
    
    public ConflictResolutionResult submitResolution(String sessionId, String fileName, String resolution) {
        GameSession session = sessions.get(sessionId);
        if (session == null) {
            return ConflictResolutionResult.error("Session not found");
        }
        
        ConflictBlock conflict = session.getConflictData().getConflicts().stream()
            .filter(c -> c.getFileName().equals(fileName))
            .findFirst()
            .orElse(null);
            
        if (conflict == null) {
            return ConflictResolutionResult.error("Conflict not found");
        }
        
        if (conflict.isResolved()) {
            return ConflictResolutionResult.error("Already resolved");
        }
        
        boolean isCorrect = validateResolution(conflict, resolution);
        conflict.setResolved(true);
        
        boolean allResolved = session.getConflictData().getConflicts().stream()
            .allMatch(ConflictBlock::isResolved);
            
        if (allResolved) {
            session.setStatus(GameStatus.COMPLETED);
            session.setEndTime(LocalDateTime.now());
        }
        
        return ConflictResolutionResult.success(isCorrect, 85, 90, allResolved);
    }
    
    public GameResult finishGame(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) {
            return null;
        }
        
        if (session.getStatus() == GameStatus.IN_PROGRESS) {
            session.setStatus(GameStatus.COMPLETED);
            session.setEndTime(LocalDateTime.now());
        }
        
        long elapsedTime = ChronoUnit.SECONDS.between(session.getStartTime(), 
            session.getEndTime() != null ? session.getEndTime() : LocalDateTime.now());
        
        int resolvedCount = (int) session.getConflictData().getConflicts().stream()
            .mapToLong(c -> c.isResolved() ? 1 : 0).sum();
        
        int totalScore = BASE_SCORE + (resolvedCount * 100) - ((int)elapsedTime);
        
        return GameResult.builder()
            .sessionId(sessionId)
            .userId(session.getUserId())
            .difficulty(session.getDifficulty())
            .totalScore(Math.max(0, totalScore))
            .correctCount(resolvedCount)
            .totalConflicts(session.getConflictData().getTotalConflicts())
            .accuracy(85)
            .cleanliness(90)
            .elapsedTime(elapsedTime)
            .status(session.getStatus())
            .build();
    }
    
    public GameResultWithAchievements finishGameWithAchievements(String sessionId) {
        GameResult result = finishGame(sessionId);
        if (result == null) return null;
        
        List<AchievementService.AchievementUnlocked> achievements = 
            achievementService.checkAchievements(result.getUserId(), result);
        
        return new GameResultWithAchievements(result, achievements);
    }
    
    private boolean validateResolution(ConflictBlock conflict, String resolution) {
        String normalized = resolution.replaceAll("\\s+", " ").trim();
        String expected = conflict.getExpectedResolution().replaceAll("\\s+", " ").trim();
        return normalized.equals(expected);
    }
    
    public static class GameSession {
        private String sessionId;
        private Long userId;
        private Difficulty difficulty;
        private ConflictData conflictData;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private int timeLimit;
        private GameStatus status;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Difficulty getDifficulty() { return difficulty; }
        public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
        public ConflictData getConflictData() { return conflictData; }
        public void setConflictData(ConflictData conflictData) { this.conflictData = conflictData; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public int getTimeLimit() { return timeLimit; }
        public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }
        public GameStatus getStatus() { return status; }
        public void setStatus(GameStatus status) { this.status = status; }
    }
    
    public static class ConflictResolutionResult {
        private final boolean success;
        private final boolean correct;
        private final int accuracy;
        private final int cleanliness;
        private final boolean gameCompleted;
        private final String errorMessage;
        
        private ConflictResolutionResult(boolean success, boolean correct, int accuracy, int cleanliness, boolean gameCompleted, String errorMessage) {
            this.success = success;
            this.correct = correct;
            this.accuracy = accuracy;
            this.cleanliness = cleanliness;
            this.gameCompleted = gameCompleted;
            this.errorMessage = errorMessage;
        }
        
        public static ConflictResolutionResult success(boolean correct, int accuracy, int cleanliness, boolean gameCompleted) {
            return new ConflictResolutionResult(true, correct, accuracy, cleanliness, gameCompleted, null);
        }
        
        public static ConflictResolutionResult error(String message) {
            return new ConflictResolutionResult(false, false, 0, 0, false, message);
        }
        
        public boolean isSuccess() { return success; }
        public boolean isCorrect() { return correct; }
        public int getAccuracy() { return accuracy; }
        public int getCleanliness() { return cleanliness; }
        public boolean isGameCompleted() { return gameCompleted; }
        public String getErrorMessage() { return errorMessage; }
    }
    
    public static class GameResult {
        private String sessionId;
        private Long userId;
        private Difficulty difficulty;
        private int totalScore;
        private int correctCount;
        private int totalConflicts;
        private int accuracy;
        private int cleanliness;
        private long elapsedTime;
        private GameStatus status;
        
        public static GameResultBuilder builder() { return new GameResultBuilder(); }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Difficulty getDifficulty() { return difficulty; }
        public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
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
        public GameStatus getStatus() { return status; }
        public void setStatus(GameStatus status) { this.status = status; }
        
        public static class GameResultBuilder {
            private GameResult result = new GameResult();
            
            public GameResultBuilder sessionId(String sessionId) { result.sessionId = sessionId; return this; }
            public GameResultBuilder userId(Long userId) { result.userId = userId; return this; }
            public GameResultBuilder difficulty(Difficulty difficulty) { result.difficulty = difficulty; return this; }
            public GameResultBuilder totalScore(int totalScore) { result.totalScore = totalScore; return this; }
            public GameResultBuilder correctCount(int correctCount) { result.correctCount = correctCount; return this; }
            public GameResultBuilder totalConflicts(int totalConflicts) { result.totalConflicts = totalConflicts; return this; }
            public GameResultBuilder accuracy(int accuracy) { result.accuracy = accuracy; return this; }
            public GameResultBuilder cleanliness(int cleanliness) { result.cleanliness = cleanliness; return this; }
            public GameResultBuilder elapsedTime(long elapsedTime) { result.elapsedTime = elapsedTime; return this; }
            public GameResultBuilder status(GameStatus status) { result.status = status; return this; }
            
            public GameResult build() { return result; }
        }
    }
    
    public static class GameResultWithAchievements {
        private final GameResult gameResult;
        private final List<AchievementService.AchievementUnlocked> newAchievements;
        
        public GameResultWithAchievements(GameResult gameResult, List<AchievementService.AchievementUnlocked> newAchievements) {
            this.gameResult = gameResult;
            this.newAchievements = newAchievements;
        }
        
        public GameResult getGameResult() { return gameResult; }
        public List<AchievementService.AchievementUnlocked> getNewAchievements() { return newAchievements; }
        public boolean hasNewAchievements() { return !newAchievements.isEmpty(); }
    }
    
    public enum GameStatus {
        IN_PROGRESS, COMPLETED, TIME_OUT, ABANDONED
    }
}
