package com.gitmerge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String nickname;
    private Integer mmr;
    private Long totalScore;
    private Integer wins;
    private Integer losses;
    private LocalDateTime createdAt;
}
