package com.gitmerge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "seasons")
@Getter @Setter
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "season_seq")
    @SequenceGenerator(name = "season_seq", sequenceName = "seq_season_id", allocationSize = 1)
    private Long id;
    
    @Column(name = "season_name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;
    
    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;
    
    @Column(nullable = false)
    private Boolean active = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
