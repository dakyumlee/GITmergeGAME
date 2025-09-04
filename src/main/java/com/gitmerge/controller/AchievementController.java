package com.gitmerge.controller;

import com.gitmerge.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = "*")
public class AchievementController {
    
    @Autowired
    private AchievementService achievementService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAchievements(@PathVariable Long userId) {
        try {
            var achievements = achievementService.getUserAchievements(userId);
            int totalPoints = achievementService.getTotalAchievementPoints(userId);
            
            return ResponseEntity.ok(Map.of(
                "achievements", achievements,
                "totalPoints", totalPoints
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
