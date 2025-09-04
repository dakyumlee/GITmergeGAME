package com.gitmerge.repository;

import com.gitmerge.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUserId(Long userId);
    
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.achievement.code = :code")
    boolean existsByUserIdAndAchievementCode(Long userId, String code);
}
