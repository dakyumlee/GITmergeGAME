package com.gitmerge.entity;

import com.gitmerge.enums.ResultStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Getter @Setter
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "result_seq")
    @SequenceGenerator(name = "result_seq", sequenceName = "seq_result_id", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;
    
    @Column(nullable = false)
    private Integer score;
    
    @Column(name = "time_taken_ms", nullable = false)
    private Long timeTakenMs;
    
    @Column(name = "build_passed", nullable = false)
    private Boolean buildPassed;
    
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal accuracy;
    
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal cleanliness;
    
    @Column(nullable = false)
    private Integer retries = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ResultStatus status;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
