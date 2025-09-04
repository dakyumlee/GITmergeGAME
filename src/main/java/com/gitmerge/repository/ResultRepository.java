package com.gitmerge.repository;

import com.gitmerge.entity.Result;
import com.gitmerge.enums.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Result> findByGameIdOrderByScoreDesc(Long gameId);
    
    @Query("SELECT r FROM Result r WHERE r.user.id = :userId AND r.status = :status")
    List<Result> findByUserAndStatus(Long userId, ResultStatus status);
    
    @Query("SELECT r FROM Result r WHERE r.game.id = :gameId ORDER BY r.score DESC, r.timeTakenMs ASC")
    List<Result> findGameLeaderboard(Long gameId);
    
    @Query("SELECT COUNT(r) FROM Result r WHERE r.user.id = :userId AND r.status = 'SUCCESS'")
    Long countSuccessfulResults(Long userId);
}
