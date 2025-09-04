package com.gitmerge.dto;

import lombok.Data;

@Data
public class RankingDto {
    private Integer rank;
    private String nickname;
    private Integer score;
    private Integer mmr;
    private Long seasonScore;
    private Integer wins;
    private Integer losses;
}
