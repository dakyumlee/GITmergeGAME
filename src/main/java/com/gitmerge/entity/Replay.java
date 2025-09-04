package com.gitmerge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "replays")
@Getter @Setter
public class Replay {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "replay_seq")
    @SequenceGenerator(name = "replay_seq", sequenceName = "seq_replay_id", allocationSize = 1)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false, unique = true)
    private Result result;
    
    @Lob
    @Column(name = "action_log", nullable = false)
    private String actionLog;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
