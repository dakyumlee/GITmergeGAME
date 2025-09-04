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
public class QuickGameService {
    
    @Autowired
    private ConflictGeneratorService conflictGenerator;
    
    private Map<String, QuickGameSession> sessions = new ConcurrentHashMap<>();
    
    public QuickGameSession startQuickGame(String nickname, Difficulty difficulty) {
        ConflictData conflictData = conflictGenerator.generateConflictByDifficulty(difficulty);
        
        QuickGameSession session = new QuickGameSession();
        session.sessionId = conflictData.getSessionId();
        session.nickname = nickname;
        session.difficulty = difficulty;
        session.conflicts = conflictData.getConflicts();
        session.startTime = LocalDateTime.now();
        session.currentIndex = 0;
        session.correctCount = 0;
        session.wrongCount = 0;
        session.finished = false;
        
        sessions.put(session.sessionId, session);
        return session;
    }
    
    public SubmitResult submitAnswer(String sessionId, String answer) {
        QuickGameSession session = sessions.get(sessionId);
        if (session == null || session.finished) {
            return new SubmitResult(false, "세션을 찾을 수 없습니다", false);
        }
        
        ConflictBlock current = session.conflicts.get(session.currentIndex);
        boolean correct = validateAnswer(current, answer);
        
        if (correct) {
            session.correctCount++;
        } else {
            session.wrongCount++;
        }
        
        session.currentIndex++;
        
        boolean gameOver = session.currentIndex >= session.conflicts.size() || 
                          session.wrongCount >= 3 || 
                          session.correctCount >= session.conflicts.size();
        
        if (gameOver) {
            session.finished = true;
            session.endTime = LocalDateTime.now();
        }
        
        String message = correct ? "정답입니다!" : "틀렸습니다. 정답: " + current.getExpectedResolution();
        return new SubmitResult(correct, message, gameOver);
    }
    
    public GameResult getGameResult(String sessionId) {
        QuickGameSession session = sessions.get(sessionId);
        if (session == null) return null;
        
        long elapsed = ChronoUnit.SECONDS.between(session.startTime, 
            session.endTime != null ? session.endTime : LocalDateTime.now());
        
        int score = session.correctCount * 100 - session.wrongCount * 50 - (int)elapsed;
        
        return new GameResult(sessionId, session.nickname, session.difficulty.toString(),
                             Math.max(0, score), session.correctCount, session.wrongCount, 
                             session.conflicts.size(), elapsed);
    }
    
    private boolean validateAnswer(ConflictBlock conflict, String answer) {
        return answer.trim().equals(conflict.getExpectedResolution().trim());
    }
    
    public static class QuickGameSession {
        public String sessionId;
        public String nickname;
        public Difficulty difficulty;
        public List<ConflictBlock> conflicts;
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public int currentIndex;
        public int correctCount;
        public int wrongCount;
        public boolean finished;
    }
    
    public static class SubmitResult {
        public boolean correct;
        public String message;
        public boolean gameOver;
        
        public SubmitResult(boolean correct, String message, boolean gameOver) {
            this.correct = correct;
            this.message = message;
            this.gameOver = gameOver;
        }
    }
    
    public static class GameResult {
        public String sessionId;
        public String nickname;
        public String difficulty;
        public int totalScore;
        public int correctCount;
        public int wrongCount;
        public int totalQuestions;
        public long elapsedSeconds;
        
        public GameResult(String sessionId, String nickname, String difficulty, 
                         int totalScore, int correctCount, int wrongCount, 
                         int totalQuestions, long elapsedSeconds) {
            this.sessionId = sessionId;
            this.nickname = nickname;
            this.difficulty = difficulty;
            this.totalScore = totalScore;
            this.correctCount = correctCount;
            this.wrongCount = wrongCount;
            this.totalQuestions = totalQuestions;
            this.elapsedSeconds = elapsedSeconds;
        }
    }
}
