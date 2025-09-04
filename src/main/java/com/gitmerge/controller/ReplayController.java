package com.gitmerge.controller;

import com.gitmerge.entity.Replay;
import com.gitmerge.service.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/replays")
@RequiredArgsConstructor
public class ReplayController {
    private final ReplayService replayService;
    
    @GetMapping("/{resultId}")
    public ResponseEntity<Replay> getReplay(@PathVariable Long resultId) {
        return replayService.getReplay(resultId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
