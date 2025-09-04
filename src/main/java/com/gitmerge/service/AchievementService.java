package com.gitmerge.service;

import com.gitmerge.entity.*;
import com.gitmerge.enums.ResultStatus;
import com.gitmerge.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ResultRepository resultRepository;
    
    public void checkAndGrant(User user, Result result) {
        if (result.getStatus() == ResultStatus.SUCCESS) {
            checkFirstMerge(user);
            checkSpeedDemon(user, result);
            checkPerfectionist(user, result);
            checkMergeMaster(user);
        }
    }
    
    private void checkFirstMerge(User user) {
        if (!hasAchievement(user.getId(), "FIRST_MERGE")) {
            grantAchievement(user, "FIRST_MERGE");
        }
    }
    
    private void checkSpeedDemon(User user, Result result) {
        if (result.getTimeTakenMs() <= 120000 && !hasAchievement(user.getId(), "SPEED_DEMON")) {
            grantAchievement(user, "SPEED_DEMON");
        }
    }
    
    private void checkPerfectionist(User user, Result result) {
        if (result.getAccuracy().doubleValue() >= 1.0 && !hasAchievement(user.getId(), "PERFECTIONIST")) {
            grantAchievement(user, "PERFECTIONIST");
        }
    }
    
    private void checkMergeMaster(User user) {
        Long successCount = resultRepository.countSuccessfulResults(user.getId());
        if (successCount >= 10 && !hasAchievement(user.getId(), "MERGE_MASTER")) {
            grantAchievement(user, "MERGE_MASTER");
        }
    }
    
    private boolean hasAchievement(Long userId, String code) {
        return userAchievementRepository.existsByUserIdAndAchievementCode(userId, code);
    }
    
    private void grantAchievement(User user, String code) {
        Achievement achievement = achievementRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Achievement not found: " + code));
        
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setAchievement(achievement);
        userAchievementRepository.save(userAchievement);
    }
}
