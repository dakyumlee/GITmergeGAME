package com.gitmerge.service;

import com.gitmerge.entity.*;
import com.gitmerge.enums.ResultStatus;
import com.gitmerge.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayService {
    private final ResultRepository resultRepository;
    private final AchievementService achievementService;
    
    public Result submitResult(User user, Game game, Match match, 
                              Long timeTakenMs, Boolean buildPassed, 
                              BigDecimal accuracy, BigDecimal cleanliness, 
                              Integer retries, ResultStatus status) {
        
        Integer score = calculateScore(timeTakenMs, buildPassed, accuracy, cleanliness, retries);
        
        Result result = new Result();
        result.setUser(user);
        result.setGame(game);
        result.setMatch(match);
        result.setScore(score);
        result.setTimeTakenMs(timeTakenMs);
        result.setBuildPassed(buildPassed);
        result.setAccuracy(accuracy);
        result.setCleanliness(cleanliness);
        result.setRetries(retries);
        result.setStatus(status);
        
        Result savedResult = resultRepository.save(result);
        
        // achievementService.checkAchievements(user.getId(), savedResult);
        
        return savedResult;
    }
    
    private Integer calculateScore(Long timeTakenMs, Boolean buildPassed, 
                                  BigDecimal accuracy, BigDecimal cleanliness, Integer retries) {
        int base = buildPassed ? 1000 : 0;
        int speed = Math.max(0, (600000 - timeTakenMs.intValue()) / 100);
        int accuracyPoints = accuracy.multiply(BigDecimal.valueOf(400)).intValue();
        int cleanPoints = cleanliness.multiply(BigDecimal.valueOf(200)).intValue();
        int penalty = retries * 50;
        
        return base + speed + accuracyPoints + cleanPoints - penalty;
    }
}
