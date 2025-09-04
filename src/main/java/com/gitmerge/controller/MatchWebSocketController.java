package com.gitmerge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class MatchWebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private static Map<String, GameRoom> gameRooms = new ConcurrentHashMap<>();
    private static Map<String, String> playerRooms = new ConcurrentHashMap<>();
    
    @MessageMapping("/match/join")
    public void joinMatch(Map<String, Object> message) {
        String playerId = (String) message.get("playerId");
        String playerName = (String) message.get("playerName");
        String difficulty = (String) message.get("difficulty");
        
        GameRoom room = findOrCreateRoom(difficulty);
        room.addPlayer(playerId, playerName);
        playerRooms.put(playerId, room.roomId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "ROOM_UPDATE");
        response.put("roomId", room.roomId);
        response.put("players", room.getPlayerList());
        response.put("playerCount", room.players.size());
        
        messagingTemplate.convertAndSend("/topic/room/" + room.roomId, response);
    }
    
    @MessageMapping("/match/ready")
    public void playerReady(Map<String, Object> message) {
        String playerId = (String) message.get("playerId");
        String roomId = playerRooms.get(playerId);
        
        if (roomId != null) {
            GameRoom room = gameRooms.get(roomId);
            if (room != null) {
                room.setPlayerReady(playerId);
                
                Map<String, Object> response = new HashMap<>();
                response.put("type", "PLAYER_READY");
                response.put("playerId", playerId);
                response.put("readyCount", room.getReadyCount());
                response.put("totalPlayers", room.players.size());
                
                if (room.allPlayersReady() && room.players.size() >= 2) {
                    response.put("gameStarting", true);
                    room.gameStarted = true;
                }
                
                messagingTemplate.convertAndSend("/topic/room/" + roomId, response);
            }
        }
    }
    
    private GameRoom findOrCreateRoom(String difficulty) {
        for (GameRoom room : gameRooms.values()) {
            if (room.difficulty.equals(difficulty) && room.players.size() < 8 && !room.gameStarted) {
                return room;
            }
        }
        
        String roomId = "room_" + System.currentTimeMillis();
        GameRoom newRoom = new GameRoom(roomId, difficulty);
        gameRooms.put(roomId, newRoom);
        return newRoom;
    }
    
    static class GameRoom {
        String roomId;
        String difficulty;
        Map<String, String> players = new ConcurrentHashMap<>();
        Set<String> readyPlayers = ConcurrentHashMap.newKeySet();
        boolean gameStarted = false;
        
        GameRoom(String roomId, String difficulty) {
            this.roomId = roomId;
            this.difficulty = difficulty;
        }
        
        void addPlayer(String playerId, String playerName) {
            players.put(playerId, playerName);
        }
        
        void setPlayerReady(String playerId) {
            readyPlayers.add(playerId);
        }
        
        int getReadyCount() {
            return readyPlayers.size();
        }
        
        boolean allPlayersReady() {
            return readyPlayers.size() == players.size();
        }
        
        List<Map<String, String>> getPlayerList() {
            List<Map<String, String>> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : players.entrySet()) {
                Map<String, String> player = new HashMap<>();
                player.put("id", entry.getKey());
                player.put("name", entry.getValue());
                player.put("ready", readyPlayers.contains(entry.getKey()) ? "true" : "false");
                list.add(player);
            }
            return list;
        }
    }
}
