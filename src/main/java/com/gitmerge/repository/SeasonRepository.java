package com.gitmerge.repository;

import com.gitmerge.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    @Query("SELECT s FROM Season s WHERE s.active = true")
    Optional<Season> findActiveSeason();
    
    @Query("SELECT s FROM Season s ORDER BY s.startsAt DESC")
    Season findLatestSeason();
}
