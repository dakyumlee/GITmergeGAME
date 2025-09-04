package com.gitmerge.service;

import com.gitmerge.entity.Game;
import com.gitmerge.enums.Difficulty;
import com.gitmerge.enums.GameMode;
import com.gitmerge.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    
    public Game createGame(String seed, Difficulty difficulty, GameMode gameMode) {
        Game game = new Game();
        game.setSeed(seed);
        game.setDifficulty(difficulty);
        game.setGameMode(gameMode);
        game.setConflictPack(generateConflictPack(difficulty));
        return gameRepository.save(game);
    }
    
    public String getDailySeed() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    
    public Optional<Game> findBySeed(String seed) {
        return gameRepository.findBySeed(seed);
    }
    
    private String generateConflictPack(Difficulty difficulty) {
        return "{\"conflicts\": [{\"type\": \"DEFAULT\", \"complexity\": " + 
               (difficulty.ordinal() + 1) * 3 + "}]}";
    }
}
