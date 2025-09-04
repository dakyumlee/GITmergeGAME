package com.gitmerge.service;

import com.gitmerge.entity.Achievement;
import com.gitmerge.entity.UserAchievement;
import com.gitmerge.entity.User;
import com.gitmerge.repository.AchievementRepository;
import com.gitmerge.repository.UserAchievementRepository;
import com.gitmerge.repository.UserRepository;
import com.gitmerge.service.GameLogicService.GameResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AchievementService {
    
    @Autowired
    private AchievementRepository achievementRepository;
    
    @Autowired
    private UserAchievementRepository userAchievementRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<AchievementUnlocked> checkAchievements(Long userId, GameResult gameResult) {
        List<AchievementUnlocked> unlockedList = new ArrayList<>();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return unlockedList;
        
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<Long> userAchievementIds = userAchievementRepository.findByUserId(userId)
            .stream().map(ua -> ua.getAchievement().getId()).toList();
        
        for (Achievement achievement : allAchievements) {
            if (userAchievementIds.contains(achievement.getId())) continue;
            
            if (checkAchievementCondition(achievement, gameResult)) {
                UserAchievement userAchievement = new UserAchievement();
                userAchievement.setUser(user);
                userAchievement.setAchievement(achievement);
                userAchievementRepository.save(userAchievement);
                
                unlockedList.add(new AchievementUnlocked(
                    achievement.getId(),
                    achievement.getName(),
                    achievement.getDescription(),
                    getIconForAchievement(achievement.getName()),
                    achievement.getPoints()
                ));
            }
        }
        
        return unlockedList;
    }
    
    private String getIconForAchievement(String name) {
        return switch (name) {
            case "첫 머지" -> "🎯";
            case "스피드 데몬" -> "⚡";
            case "완벽주의자" -> "💎";
            case "머지의 제왕" -> "👑";
            case "리베이스 생존자" -> "🔥";
            default -> "🏆";
        };
    }
    
    private boolean checkAchievementCondition(Achievement achievement, GameResult gameResult) {
        return switch (achievement.getName()) {
            case "첫 머지" -> gameResult.getCorrectCount() >= 1;
            case "스피드 데몬" -> gameResult.getElapsedTime() <= 60;
            case "완벽주의자" -> gameResult.getAccuracy() >= 95;
            case "머지의 제왕" -> gameResult.getTotalScore() >= 2000;
            case "리베이스 생존자" -> gameResult.getDifficulty().toString().equals("HELL");
            default -> false;
        };
    }
    
    public List<UserAchievementInfo> getUserAchievements(Long userId) {
        return userAchievementRepository.findByUserId(userId)
            .stream()
            .map(ua -> new UserAchievementInfo(
                ua.getAchievement().getId(),
                ua.getAchievement().getName(),
                ua.getAchievement().getDescription(),
                getIconForAchievement(ua.getAchievement().getName()),
                ua.getAchievement().getPoints(),
                ua.getEarnedAt()
            ))
            .toList();
    }
    
    public int getTotalAchievementPoints(Long userId) {
        return userAchievementRepository.findByUserId(userId)
            .stream()
            .mapToInt(ua -> ua.getAchievement().getPoints())
            .sum();
    }
    
    public static class AchievementUnlocked {
        private final Long id;
        private final String name;
        private final String description;
        private final String icon;
        private final int points;
        
        public AchievementUnlocked(Long id, String name, String description, String icon, int points) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.points = points;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        public int getPoints() { return points; }
    }
    
    public static class UserAchievementInfo {
        private final Long id;
        private final String name;
        private final String description;
        private final String icon;
        private final int points;
        private final LocalDateTime unlockedAt;
        
        public UserAchievementInfo(Long id, String name, String description, String icon, int points, LocalDateTime unlockedAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.points = points;
            this.unlockedAt = unlockedAt;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        public int getPoints() { return points; }
        public LocalDateTime getUnlockedAt() { return unlockedAt; }
    }
}
