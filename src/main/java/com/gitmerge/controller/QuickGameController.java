package com.gitmerge.controller;

import com.gitmerge.service.ConflictGeneratorService;
import com.gitmerge.enums.Difficulty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/quick")
@CrossOrigin(origins = "*")
public class QuickGameController {
    
    @Autowired
    private ConflictGeneratorService conflictGenerator;
    
    private Map<String, QuickGameSession> sessions = new ConcurrentHashMap<>();
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startQuickGame(@RequestBody Map<String, Object> request) {
        try {
            String nickname = (String) request.get("nickname");
            String difficultyStr = (String) request.get("difficulty");
            Difficulty difficulty = Difficulty.valueOf(difficultyStr);
            
            String sessionId = UUID.randomUUID().toString();
            var conflictData = conflictGenerator.generateConflictByDifficulty(difficulty);
            
            QuickGameSession session = new QuickGameSession();
            session.sessionId = sessionId;
            session.nickname = nickname;
            session.conflicts = conflictData.getConflicts();
            session.currentIndex = 0;
            session.correctCount = 0;
            session.wrongCount = 0;
            session.startTime = System.currentTimeMillis();
            session.gameOver = false;
            
            sessions.put(sessionId, session);
            
            return ResponseEntity.ok(Map.of(
                "sessionId", sessionId,
                "currentConflict", session.conflicts.get(0),
                "totalConflicts", session.conflicts.size(),
                "currentIndex", 0
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> request) {
        try {
            String sessionId = (String) request.get("sessionId");
            String answer = (String) request.get("answer");
            
            QuickGameSession session = sessions.get(sessionId);
            if (session == null || session.gameOver) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid session"));
            }
            
            var currentConflict = session.conflicts.get(session.currentIndex);
            boolean correct = validateAnswer(answer, currentConflict.getExpectedResolution());
            
            if (correct) {
                session.correctCount++;
            } else {
                session.wrongCount++;
            }
            
            session.currentIndex++;
            
            boolean gameOver = session.wrongCount >= 3 || 
                              session.currentIndex >= session.conflicts.size();
            session.gameOver = gameOver;
            
            Map<String, Object> response = new HashMap<>();
            response.put("correct", correct);
            response.put("message", correct ? "정답입니다!" : "틀렸습니다! 정답: " + normalizeCode(currentConflict.getExpectedResolution()));
            response.put("gameOver", gameOver);
            
            if (!gameOver && session.currentIndex < session.conflicts.size()) {
                response.put("nextConflict", session.conflicts.get(session.currentIndex));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/result/{sessionId}")
    public ResponseEntity<Map<String, Object>> getGameResult(@PathVariable String sessionId) {
        try {
            QuickGameSession session = sessions.get(sessionId);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }
            
            long elapsedMs = System.currentTimeMillis() - session.startTime;
            int elapsedSeconds = (int) (elapsedMs / 1000);
            int totalScore = session.correctCount * 100 - session.wrongCount * 50 - elapsedSeconds;
            
            return ResponseEntity.ok(Map.of(
                "totalScore", Math.max(0, totalScore),
                "correctCount", session.correctCount,
                "wrongCount", session.wrongCount,
                "totalQuestions", session.conflicts.size(),
                "elapsedSeconds", elapsedSeconds,
                "nickname", session.nickname
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private boolean validateAnswer(String userAnswer, String expectedAnswer) {
        String normalizedUser = normalizeCode(userAnswer);
        String normalizedExpected = normalizeCode(expectedAnswer);
        
        boolean exactMatch = normalizedUser.equals(normalizedExpected);
        if (exactMatch) return true;
        
        boolean hasConflictMarkers = userAnswer.contains("<<<<<<<") || 
                                    userAnswer.contains("=======") || 
                                    userAnswer.contains(">>>>>>>");
        if (hasConflictMarkers) return false;
        
        String[] userLines = normalizedUser.split("\n");
        String[] expectedLines = normalizedExpected.split("\n");
        
        if (userLines.length != expectedLines.length) return false;
        
        for (int i = 0; i < userLines.length; i++) {
            String userLine = userLines[i].trim();
            String expectedLine = expectedLines[i].trim();
            
            if (!userLine.equals(expectedLine)) {
                return false;
            }
        }
        
        return true;
    }
    
    private String normalizeCode(String code) {
        return code.trim()
                   .replaceAll("\\s+", " ")
                   .replaceAll("\\s*([{}();,])\\s*", "$1")
                   .replaceAll("\\s*([=+\\-*/])\\s*", " $1 ");
    }
    
    public static class QuickGameSession {
        public String sessionId;
        public String nickname;
        public List<ConflictGeneratorService.ConflictBlock> conflicts;
        public int currentIndex;
        public int correctCount;
        public int wrongCount;
        public long startTime;
        public boolean gameOver;
    }
}
