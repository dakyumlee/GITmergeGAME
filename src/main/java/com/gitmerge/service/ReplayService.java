package com.gitmerge.service;

import com.gitmerge.entity.Replay;
import com.gitmerge.entity.Result;
import com.gitmerge.repository.ReplayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplayService {
    private final ReplayRepository replayRepository;
    
    public Replay saveReplay(Result result, String actionLog) {
        Replay replay = new Replay();
        replay.setResult(result);
        replay.setActionLog(actionLog);
        return replayRepository.save(replay);
    }
    
    public Optional<Replay> getReplay(Long resultId) {
        return replayRepository.findByResultId(resultId);
    }
}
