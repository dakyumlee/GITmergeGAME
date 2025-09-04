package com.gitmerge.dto;

import com.gitmerge.enums.MatchStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatchDto {
    private Long id;
    private Long gameId;
    private MatchStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
}
