package com.gitmerge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class BattleController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private Map<String, BattleRoom> battleRooms = new ConcurrentHashMap<>();
    private Map<String, String> userRooms = new ConcurrentHashMap<>();
    
    private static final String[] ADJECTIVES = {
        "빠른", "강한", "똑똑한", "용감한", "멋진", "재미있는", "신나는", "화난", "졸린", "배고픈",
        "행복한", "슬픈", "놀란", "당황한", "신기한", "무서운", "친절한", "시끄러운", "조용한", "느린"
    };
    
    private static final String[] NOUNS = {
        "개발자", "토끼", "고양이", "강아지", "사자", "호랑이", "곰", "팬더", "코끼리", "기린",
        "펭귄", "돌고래", "상어", "독수리", "부엉이", "다람쥐", "햄스터", "거북이", "여우", "늑대"
    };
    
    private String generateFriendlyRoomId() {
        Random random = new Random();
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        int number = random.nextInt(99) + 1;
        
        String roomId = adjective + noun + number;
        
        if (battleRooms.containsKey(roomId)) {
            return generateFriendlyRoomId();
        }
        
        return roomId;
    }
    
    @PostMapping("/api/battle/create")
    @ResponseBody
    public Map<String, Object> createRoom(@RequestBody Map<String, Object> request) {
        String nickname = (String) request.get("nickname");
        String difficulty = (String) request.get("difficulty");
        
        String roomId = generateFriendlyRoomId();
        BattleRoom room = new BattleRoom(roomId, nickname, difficulty);
        battleRooms.put(roomId, room);
        userRooms.put(nickname, roomId);
        
        return Map.of("roomId", roomId, "status", "created");
    }
    
    @PostMapping("/api/battle/join")
    @ResponseBody
    public Map<String, Object> joinRoom(@RequestBody Map<String, Object> request) {
        String nickname = (String) request.get("nickname");
        String roomId = (String) request.get("roomId");
        
        BattleRoom room = battleRooms.get(roomId);
        if (room == null) {
            return Map.of("error", "방을 찾을 수 없습니다");
        }
        
        if (room.getPlayerCount() >= 2) {
            return Map.of("error", "방이 가득 찼습니다");
        }
        
        room.addPlayer(nickname);
        userRooms.put(nickname, roomId);
        
        messagingTemplate.convertAndSend("/topic/battle/" + roomId, 
            Map.of("type", "player_joined", "nickname", nickname, "playerCount", room.getPlayerCount()));
        
        if (room.getPlayerCount() == 2) {
            room.setStatus("ready");
            messagingTemplate.convertAndSend("/topic/battle/" + roomId, 
                Map.of("type", "room_ready", "players", room.getPlayers()));
        }
        
        return Map.of("roomId", roomId, "status", "joined", "playerCount", room.getPlayerCount());
    }
    
    @MessageMapping("/battle/start")
    @SendTo("/topic/battle/{roomId}")
    public Map<String, Object> startBattle(Map<String, Object> message) {
        String roomId = (String) message.get("roomId");
        BattleRoom room = battleRooms.get(roomId);
        
        if (room != null && room.getPlayerCount() == 2) {
            room.setStatus("playing");
            return Map.of("type", "game_started", "conflicts", generateConflicts());
        }
        
        return Map.of("type", "error", "message", "게임을 시작할 수 없습니다");
    }
    
    @MessageMapping("/battle/solve")
    public void submitSolution(Map<String, Object> message) {
        String roomId = (String) message.get("roomId");
        String nickname = (String) message.get("nickname");
        String solution = (String) message.get("solution");
        Integer conflictIndex = (Integer) message.get("conflictIndex");
        
        BattleRoom room = battleRooms.get(roomId);
        if (room != null) {
            boolean correct = validateSolution(solution, conflictIndex);
            
            if (correct) {
                room.addScore(nickname, 100);
                messagingTemplate.convertAndSend("/topic/battle/" + roomId, 
                    Map.of("type", "solution_correct", "nickname", nickname, "score", room.getScore(nickname)));
            }
            
            messagingTemplate.convertAndSend("/topic/battle/" + roomId, 
                Map.of("type", "progress_update", "nickname", nickname, "conflictIndex", conflictIndex, "correct", correct));
        }
    }
    
    @MessageMapping("/battle/finish")
    public void finishBattle(Map<String, Object> message) {
        String roomId = (String) message.get("roomId");
        String nickname = (String) message.get("nickname");
        
        BattleRoom room = battleRooms.get(roomId);
        if (room != null) {
            room.setFinished(nickname);
            
            if (room.isAllFinished()) {
                String winner = room.getWinner();
                messagingTemplate.convertAndSend("/topic/battle/" + roomId, 
                    Map.of("type", "game_finished", "winner", winner, "scores", room.getScores()));
                
                battleRooms.remove(roomId);
                room.getPlayers().forEach(player -> userRooms.remove(player));
            }
        }
    }
    
    private List<Map<String, Object>> generateConflicts() {
        List<Map<String, Object>> conflicts = new ArrayList<>();
        String[] files = {"utils.js", "service.js", "config.js"};
        String[] templates = {
            "function calculateTotal(items) {\n    return items.reduce((sum, item) => sum + item.price, 0);\n}",
            "class UserService {\n    getUserById(id) {\n        return this.repository.find(id);\n    }\n}",
            "const config = {\n    apiUrl: 'https://api.example.com',\n    timeout: 5000\n};"
        };
        
        for (int i = 0; i < 3; i++) {
            Map<String, Object> conflict = new HashMap<>();
            conflict.put("fileName", files[i]);
            conflict.put("conflictMarkers", generateConflictMarkers(templates[i]));
            conflict.put("expectedResolution", templates[i]);
            conflicts.add(conflict);
        }
        
        return conflicts;
    }
    
    private String generateConflictMarkers(String code) {
        String variation1 = code.replace("price", "cost");
        String variation2 = code.replace("price", "amount");
        return "<<<<<<< HEAD\n" + variation1 + "\n=======\n" + variation2 + "\n>>>>>>> incoming\n";
    }
    
    private boolean validateSolution(String solution, Integer conflictIndex) {
        String[] expected = {
            "function calculateTotal(items) {\n    return items.reduce((sum, item) => sum + item.price, 0);\n}",
            "class UserService {\n    getUserById(id) {\n        return this.repository.find(id);\n    }\n}",
            "const config = {\n    apiUrl: 'https://api.example.com',\n    timeout: 5000\n};"
        };
        
        if (conflictIndex >= 0 && conflictIndex < expected.length) {
            return solution.trim().equals(expected[conflictIndex].trim());
        }
        return false;
    }
    
    public static class BattleRoom {
        private String roomId;
        private String host;
        private String difficulty;
        private List<String> players;
        private Map<String, Integer> scores;
        private Set<String> finishedPlayers;
        private String status;
        
        public BattleRoom(String roomId, String host, String difficulty) {
            this.roomId = roomId;
            this.host = host;
            this.difficulty = difficulty;
            this.players = new ArrayList<>();
            this.scores = new HashMap<>();
            this.finishedPlayers = new HashSet<>();
            this.status = "waiting";
            
            this.players.add(host);
            this.scores.put(host, 0);
        }
        
        public void addPlayer(String nickname) {
            if (players.size() < 2 && !players.contains(nickname)) {
                players.add(nickname);
                scores.put(nickname, 0);
            }
        }
        
        public void addScore(String nickname, int points) {
            scores.put(nickname, scores.getOrDefault(nickname, 0) + points);
        }
        
        public void setFinished(String nickname) {
            finishedPlayers.add(nickname);
        }
        
        public boolean isAllFinished() {
            return finishedPlayers.size() == players.size();
        }
        
        public String getWinner() {
            return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("무승부");
        }
        
        public int getPlayerCount() { return players.size(); }
        public List<String> getPlayers() { return players; }
        public Map<String, Integer> getScores() { return scores; }
        public Integer getScore(String nickname) { return scores.get(nickname); }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
