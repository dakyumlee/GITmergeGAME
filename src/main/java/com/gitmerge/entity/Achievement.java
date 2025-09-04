package com.gitmerge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievements")
@Getter @Setter
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "achievement_seq")
    @SequenceGenerator(name = "achievement_seq", sequenceName = "seq_achievement_id", allocationSize = 1)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(name = "achievement_name", nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Integer points = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
