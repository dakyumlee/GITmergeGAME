package com.gitmerge.entity;

import com.gitmerge.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Getter @Setter
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_seq")
    @SequenceGenerator(name = "match_seq", sequenceName = "seq_match_id", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MatchStatus status = MatchStatus.WAITING;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
