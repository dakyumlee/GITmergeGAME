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
   
   private final Map<String, BattleRoom> rooms = new ConcurrentHashMap<>();
   
   @MessageMapping("/room/create")
   public void createRoom(CreateRoomMessage message) {
       String roomCode = generateRoomCode();
       
       BattleRoom room = createNewRoom(roomCode, message.roomName, message.nickname);
       rooms.put(roomCode, room);
       
       Map<String, Object> response = buildRoomCreatedResponse(room);
       messagingTemplate.convertAndSend("/topic/room-created", response);
   }
   
   @MessageMapping("/room/join")
   public void joinRoom(JoinRoomMessage message) {
       BattleRoom room = rooms.get(message.roomCode);
       
       if (!canJoinRoom(room, message.nickname)) {
           sendErrorToUser(message.nickname, "방을 찾을 수 없습니다");
           return;
       }
       
       if (room.players.containsKey(message.nickname)) {
           sendExistingPlayerJoined(room);
           return;
       }
       
       addPlayerToRoom(room, message.nickname, false);
       sendPlayerJoinedMessage(room, message.nickname);
   }
   
   @MessageMapping("/room/ready")
   public void playerReady(ReadyMessage message) {
       String roomCode = findPlayerRoom(message.nickname);
       if (roomCode == null) return;
       
       BattleRoom room = rooms.get(roomCode);
       BattlePlayer player = room.players.get(message.nickname);
       
       if (player != null) {
           player.ready = !player.ready;
           sendPlayerReadyMessage(roomCode, message.nickname, player.ready, room.players);
       }
   }
   
   @MessageMapping("/room/start")
   public void startGame(StartGameMessage message) {
       String roomCode = findPlayerRoom(message.nickname);
       if (roomCode == null) return;
       
       BattleRoom room = rooms.get(roomCode);
       if (!room.hostNickname.equals(message.nickname)) return;
       
       room.gameStarted = true;
       sendGameStartMessage(roomCode);
   }
   
   private BattleRoom createNewRoom(String roomCode, String roomName, String hostNickname) {
       BattleRoom room = new BattleRoom();
       room.roomCode = roomCode;
       room.roomName = roomName;
       room.hostNickname = hostNickname;
       room.players = new ConcurrentHashMap<>();
       room.gameStarted = false;
       room.maxPlayers = 8;
       
       addPlayerToRoom(room, hostNickname, true);
       return room;
   }
   
   private void addPlayerToRoom(BattleRoom room, String nickname, boolean isHost) {
       BattlePlayer player = new BattlePlayer();
       player.nickname = nickname;
       player.isHost = isHost;
       player.ready = false;
       room.players.put(nickname, player);
   }
   
   private boolean canJoinRoom(BattleRoom room, String nickname) {
       return room != null && room.players.size() < room.maxPlayers;
   }
   
   private void sendErrorToUser(String nickname, String errorMessage) {
       messagingTemplate.convertAndSendToUser(nickname, "/queue/error", 
           Map.of("message", errorMessage));
   }
   
   private void sendExistingPlayerJoined(BattleRoom room) {
       messagingTemplate.convertAndSend("/topic/room/" + room.roomCode, Map.of(
           "type", "PLAYER_JOINED",
           "roomCode", room.roomCode,
           "roomName", room.roomName,
           "players", room.players.values()
       ));
   }
   
   private void sendPlayerJoinedMessage(BattleRoom room, String newPlayer) {
       messagingTemplate.convertAndSend("/topic/room/" + room.roomCode, Map.of(
           "type", "PLAYER_JOINED",
           "roomCode", room.roomCode,
           "roomName", room.roomName,
           "newPlayer", newPlayer,
           "players", room.players.values()
       ));
   }
   
   private void sendPlayerReadyMessage(String roomCode, String playerNickname, 
                                     boolean ready, Map<String, BattlePlayer> players) {
       messagingTemplate.convertAndSend("/topic/room/" + roomCode, Map.of(
           "type", "PLAYER_READY",
           "player", playerNickname,
           "ready", ready,
           "players", players.values()
       ));
   }
   
   private void sendGameStartMessage(String roomCode) {
       messagingTemplate.convertAndSend("/topic/room/" + roomCode, Map.of(
           "type", "GAME_START",
           "message", "게임이 시작되었습니다!"
       ));
   }
   
   private Map<String, Object> buildRoomCreatedResponse(BattleRoom room) {
       return Map.of(
           "roomCode", room.roomCode,
           "roomName", room.roomName,
           "creator", room.hostNickname,
           "players", room.players.values(),
           "message", "방이 생성되었습니다"
       );
   }
   
   private String findPlayerRoom(String nickname) {
       return rooms.values().stream()
                  .filter(room -> room.players.containsKey(nickname))
                  .map(room -> room.roomCode)
                  .findFirst()
                  .orElse(null);
   }
   
   private String generateRoomCode() {
       String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
       Random random = new Random();
       
       return random.ints(6, 0, chars.length())
                    .mapToObj(chars::charAt)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
   }
   
   public static class CreateRoomMessage {
       public String nickname;
       public String roomName;
   }
   
   public static class JoinRoomMessage {
       public String nickname;
       public String roomCode;
   }
   
   public static class ReadyMessage {
       public String nickname;
   }
   
   public static class StartGameMessage {
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
