package com.gitmerge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "seq_user_id", allocationSize = 1)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 16)
    private String nickname;
    
    @Column(nullable = false)
    private Integer mmr = 1000;
    
    @Column(name = "total_score", nullable = false)
    private Long totalScore = 0L;
    
    @Column(nullable = false)
    private Integer wins = 0;
    
    @Column(nullable = false)
    private Integer losses = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Result> results = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserAchievement> achievements = new ArrayList<>();
}
