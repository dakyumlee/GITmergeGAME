package com.gitmerge.dto;

import com.gitmerge.enums.ResultStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResultDto {
    private Long id;
    private String nickname;
    private String gameSeed;
    private Integer score;
    private Long timeTakenMs;
    private Boolean buildPassed;
    private BigDecimal accuracy;
    private BigDecimal cleanliness;
    private Integer retries;
    private ResultStatus status;
    private LocalDateTime createdAt;
}
