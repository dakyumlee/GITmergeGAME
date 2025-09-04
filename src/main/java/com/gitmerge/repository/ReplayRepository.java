package com.gitmerge.repository;

import com.gitmerge.entity.Replay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReplayRepository extends JpaRepository<Replay, Long> {
    Optional<Replay> findByResultId(Long resultId);
}
