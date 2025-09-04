package com.gitmerge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReplayDto {
    private Long id;
    private Long resultId;
    private String actionLog;
    private LocalDateTime createdAt;
}
