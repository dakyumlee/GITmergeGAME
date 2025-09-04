package com.gitmerge.service;

import com.gitmerge.entity.Match;
import com.gitmerge.entity.Game;
import com.gitmerge.entity.User;
import com.gitmerge.enums.MatchStatus;
import com.gitmerge.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MultiMatchService {
    private final MatchRepository matchRepository;
    private static final int MAX_PLAYERS_PER_ROOM = 10;
    
    public Match joinOrCreateRoom(Game game, User user) {
        List<Match> availableRooms = matchRepository.findAvailableRoomsWithSpace(game.getId(), MatchStatus.WAITING, MAX_PLAYERS_PER_ROOM);
        
        if (!availableRooms.isEmpty()) {
            Match room = availableRooms.get(0);
            return room;
        }
        
        Match newRoom = new Match();
        newRoom.setGame(game);
        newRoom.setStatus(MatchStatus.WAITING);
        return matchRepository.save(newRoom);
    }
    
    public void startRoom(Long roomId) {
        Match room = matchRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(MatchStatus.RUNNING);
        room.setStartedAt(LocalDateTime.now());
        matchRepository.save(room);
    }
    
    public List<Match> getAvailableRooms() {
        return matchRepository.findByStatusOrderByCreatedAtAsc(MatchStatus.WAITING);
    }
}
