package com.gitmerge.dto;

import com.gitmerge.enums.Difficulty;
import com.gitmerge.enums.GameMode;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GameDto {
    private Long id;
    private String seed;
    private Difficulty difficulty;
    private GameMode gameMode;
    private String conflictPack;
    private LocalDateTime createdAt;
}
