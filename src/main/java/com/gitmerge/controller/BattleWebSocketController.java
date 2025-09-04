package com.gitmerge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class BattleWebSocketController {
   
   @Autowired
   private SimpMessagingTemplate messagingTemplate;
   
   private Map<String, BattleRoom> rooms = new ConcurrentHashMap<>();
   
   @MessageMapping("/room/create")
   public void createRoom(CreateRoomMessage message) {
       String roomCode = generateRoomCode();
       
       BattleRoom room = new BattleRoom();
       room.roomCode = roomCode;
       room.roomName = message.roomName;
       room.hostNickname = message.nickname;
       room.players = new ConcurrentHashMap<>();
       room.gameStarted = false;
       room.maxPlayers = 8;
       
       BattlePlayer host = new BattlePlayer();
       host.nickname = message.nickname;
       host.isHost = true;
       host.ready = false;
       
       room.players.put(message.nickname, host);
       rooms.put(roomCode, room);
       
       Map<String, Object> response = Map.of(
           "roomCode", roomCode,
           "roomName", message.roomName,
           "creator", message.nickname,
           "message", "방이 생성되었습니다"
       );
       
       messagingTemplate.convertAndSend("/topic/room-created", response);
   }
   
   @MessageMapping("/room/join")
   public void joinRoom(JoinRoomMessage message) {
       BattleRoom room = rooms.get(message.roomCode);
       
       if (room == null) {
           messagingTemplate.convertAndSendToUser(message.nickname, "/queue/error", 
               Map.of("message", "방을 찾을 수 없습니다"));
           return;
       }
       
       if (room.players.size() >= room.maxPlayers) {
           messagingTemplate.convertAndSendToUser(message.nickname, "/queue/error", 
               Map.of("message", "방이 가득 참"));
           return;
       }
       
       BattlePlayer player = new BattlePlayer();
       player.nickname = message.nickname;
       player.isHost = false;
       player.ready = false;
       
       room.players.put(message.nickname, player);
       
       messagingTemplate.convertAndSend("/topic/room/" + message.roomCode, Map.of(
           "type", "PLAYER_JOINED",
           "roomCode", message.roomCode,
           "roomName", room.roomName,
           "newPlayer", message.nickname,
           "players", room.players.values()
       ));
   }
   
   @MessageMapping("/room/ready")
   public void playerReady(ReadyMessage message) {
       String roomCode = findPlayerRoom(message.nickname);
       if (roomCode == null) return;
       
       BattleRoom room = rooms.get(roomCode);
       BattlePlayer player = room.players.get(message.nickname);
       if (player != null) {
           player.ready = !player.ready;
       }
       
       messagingTemplate.convertAndSend("/topic/room/" + roomCode, Map.of(
           "type", "PLAYER_READY",
           "players", room.players.values()
       ));
   }
   
   private String findPlayerRoom(String nickname) {
       for (BattleRoom room : rooms.values()) {
           if (room.players.containsKey(nickname)) {
               return room.roomCode;
           }
       }
       return null;
   }
   
   private String generateRoomCode() {
       String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
       StringBuilder code = new StringBuilder();
       Random random = new Random();
       
       for (int i = 0; i < 6; i++) {
           code.append(chars.charAt(random.nextInt(chars.length())));
       }
       
       return code.toString();
   }
   
   public static class CreateRoomMessage {
       public String nickname;
       public String roomName;
       
       public CreateRoomMessage() {}
   }
   
   public static class JoinRoomMessage {
       public String nickname;
       public String roomCode;
   }
   
   public static class ReadyMessage {
       public String nickname;
   }
   
   public static class BattleRoom {
       public String roomCode;
       public String roomName;
       public String hostNickname;
       public Map<String, BattlePlayer> players;
       public boolean gameStarted;
       public int maxPlayers;
   }
   
   public static class BattlePlayer {
       public String nickname;
       public boolean isHost;
       public boolean ready;
   }
}
