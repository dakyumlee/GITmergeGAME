package com.gitmerge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "season_ranks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"season_id", "user_id"})
})
@Getter @Setter
public class SeasonRank {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "season_rank_seq")
    @SequenceGenerator(name = "season_rank_seq", sequenceName = "seq_season_rank_id", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;
    
    @Column(name = "season_score", nullable = false)
    private Long seasonScore = 0L;
    
    @UpdateTimestamp
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
}
