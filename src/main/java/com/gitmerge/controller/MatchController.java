package com.gitmerge.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
public class MatchController {
    
    @GetMapping("/rooms")
    public ResponseEntity<?> getAvailableRooms(@RequestParam(required = false) Long gameId) {
        // 임시 테스트 데이터
        List<Map<String, Object>> rooms = new ArrayList<>();
        
        Map<String, Object> room1 = Map.of(
            "matchId", 1L,
            "roomName", "easy-room-1234",
            "currentCount", 2,
            "maxCount", 8,
            "participantNames", List.of("Player1", "Player2"),
            "createdAt", "2025-09-04T14:30:00"
        );
        
        Map<String, Object> room2 = Map.of(
            "matchId", 2L,
            "roomName", "normal-room-5678",
            "currentCount", 1,
            "maxCount", 6,
            "participantNames", List.of("TestPlayer"),
            "createdAt", "2025-09-04T14:35:00"
        );
        
        rooms.add(room1);
        rooms.add(room2);
        
        return ResponseEntity.ok(Map.of(
            "rooms", rooms,
            "totalRooms", rooms.size()
        ));
    }
    
    @PostMapping("/join")
    public ResponseEntity<?> joinMatch(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(Map.of(
            "matchId", 1L,
            "roomName", "test-room-" + System.currentTimeMillis(),
            "participantCount", 1,
            "maxParticipants", 8,
            "gameStarted", false,
            "message", "방에 참가했습니다."
        ));
    }
}
