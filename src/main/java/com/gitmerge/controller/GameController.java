package com.gitmerge.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {
    
    @PostMapping("/start")
    public ResponseEntity<?> startGame(@RequestBody Map<String, Object> request) {
        
        Map<String, Object> conflict1 = Map.of(
            "fileName", "utils.js",
            "conflictMarkers", "<<<<<<< HEAD\nfunction add(a, b) { return a + b; }\n=======\nfunction add(x, y) { return x + y; }\n>>>>>>> branch",
            "expectedResolution", "function add(a, b) { return a + b; }"
        );
        
        Map<String, Object> conflict2 = Map.of(
            "fileName", "helper.js", 
            "conflictMarkers", "<<<<<<< HEAD\nconst MAX = 100;\n=======\nconst MAX = 50;\n>>>>>>> branch",
            "expectedResolution", "const MAX = 100;"
        );
        
        List<Map<String, Object>> conflicts = List.of(conflict1, conflict2);
        
        return ResponseEntity.ok(Map.of(
            "sessionId", "session_" + System.currentTimeMillis(),
            "difficulty", request.get("difficulty"),
            "conflicts", conflicts,
            "totalConflicts", conflicts.size()
        ));
    }
    
    @PostMapping("/resolve")
    public ResponseEntity<?> resolveConflict(@RequestBody Map<String, String> request) {
        String userSolution = request.get("resolution").trim();
        String sessionId = request.get("sessionId");
        
        // 간단한 정답 체크 (실제로는 더 정교해야 함)
        boolean isCorrect = userSolution.contains("function add(a, b)") || userSolution.contains("const MAX = 100");
        
        return ResponseEntity.ok(Map.of(
            "correct", isCorrect,
            "gameCompleted", false,
            "message", isCorrect ? "정답입니다!" : "다시 시도하세요"
        ));
    }
}
