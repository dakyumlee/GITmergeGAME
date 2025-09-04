package com.gitmerge.entity;

import com.gitmerge.enums.Difficulty;
import com.gitmerge.enums.GameMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Getter @Setter
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_seq")
    @SequenceGenerator(name = "game_seq", sequenceName = "seq_game_id", allocationSize = 1)
    private Long id;
    
    @Column(nullable = false)
    private String seed;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Difficulty difficulty;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "game_mode", nullable = false, length = 10)
    private GameMode gameMode;
    
    @Lob
    @Column(name = "conflict_pack")
    private String conflictPack;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
