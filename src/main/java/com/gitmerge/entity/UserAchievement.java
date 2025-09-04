package com.gitmerge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
})
@Getter @Setter
public class UserAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_achievement_seq")
    @SequenceGenerator(name = "user_achievement_seq", sequenceName = "seq_user_achievement_id", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;
    
    @CreationTimestamp
    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;
}
