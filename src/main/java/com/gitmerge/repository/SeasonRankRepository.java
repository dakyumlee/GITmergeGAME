package com.gitmerge.repository;

import com.gitmerge.entity.SeasonRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRankRepository extends JpaRepository<SeasonRank, Long> {
    @Query("SELECT sr FROM SeasonRank sr WHERE sr.season.id = :seasonId ORDER BY sr.rankPosition ASC")
    List<SeasonRank> findBySeasonIdOrderByRank(Long seasonId);
    
    @Query("SELECT sr FROM SeasonRank sr WHERE sr.season.id = :seasonId AND sr.user.id = :userId")
    Optional<SeasonRank> findBySeasonAndUser(Long seasonId, Long userId);
    
    @Query("SELECT sr FROM SeasonRank sr WHERE sr.season.active = true ORDER BY sr.rankPosition ASC")
    List<SeasonRank> findCurrentSeasonRanking();
}
