package com.gitmerge.repository;

import com.gitmerge.entity.Game;
import com.gitmerge.enums.Difficulty;
import com.gitmerge.enums.GameMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findBySeed(String seed);
    
    List<Game> findByDifficultyAndGameMode(Difficulty difficulty, GameMode gameMode);
    
    @Query("SELECT g FROM Game g WHERE g.seed LIKE :seedPattern")
    List<Game> findDailyGames(String seedPattern);
}
