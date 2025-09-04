package com.gitmerge.repository;

import com.gitmerge.entity.Match;
import com.gitmerge.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByStatusOrderByCreatedAtAsc(MatchStatus status);
    
    @Query("SELECT m FROM Match m WHERE m.game.id = :gameId AND m.status = :status")
    Optional<Match> findAvailableMatch(Long gameId, MatchStatus status);
    
    @Query("SELECT m FROM Match m WHERE m.status = 'WAITING' ORDER BY m.createdAt ASC")
    List<Match> findWaitingMatches();
    
    @Query(value = "SELECT m.* FROM matches m LEFT JOIN results r ON m.id = r.match_id WHERE m.game_id = :gameId AND m.status = :#{#status.name()} GROUP BY m.id HAVING COUNT(r.id) < :maxPlayers ORDER BY m.created_at ASC", nativeQuery = true)
    List<Match> findAvailableRoomsWithSpace(Long gameId, MatchStatus status, int maxPlayers);
}
