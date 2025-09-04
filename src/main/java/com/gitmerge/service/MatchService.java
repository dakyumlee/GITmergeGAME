package com.gitmerge.service;

import com.gitmerge.entity.Game;
import com.gitmerge.entity.Match;
import com.gitmerge.entity.User;
import com.gitmerge.enums.MatchStatus;
import com.gitmerge.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {
    private final MatchRepository matchRepository;
    
    public Match createOrJoinMatch(Game game, User user) {
        Optional<Match> waitingMatch = matchRepository.findAvailableMatch(game.getId(), MatchStatus.WAITING);
        
        if (waitingMatch.isPresent()) {
            Match match = waitingMatch.get();
            match.setStatus(MatchStatus.RUNNING);
            match.setStartedAt(LocalDateTime.now());
            return matchRepository.save(match);
        }
        
        Match newMatch = new Match();
        newMatch.setGame(game);
        newMatch.setStatus(MatchStatus.WAITING);
        return matchRepository.save(newMatch);
    }
    
    public void finishMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new IllegalArgumentException("Match not found"));
        match.setStatus(MatchStatus.DONE);
        match.setEndedAt(LocalDateTime.now());
        matchRepository.save(match);
    }
}
