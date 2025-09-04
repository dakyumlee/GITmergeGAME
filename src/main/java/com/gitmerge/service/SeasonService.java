
package com.gitmerge.service;

import com.gitmerge.entity.Season;

import com.gitmerge.entity.SeasonRank;

import com.gitmerge.entity.User;

import com.gitmerge.repository.SeasonRepository;

import com.gitmerge.repository.SeasonRankRepository;

import com.gitmerge.repository.UserRepository;

import com.gitmerge.repository.ResultRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.Optional;

import java.util.concurrent.atomic.AtomicInteger;

@Service

@RequiredArgsConstructor

@Transactional

public class SeasonService {

    private final SeasonRepository seasonRepository;

    private final SeasonRankRepository seasonRankRepository;

    private final UserRepository userRepository;

    private final ResultRepository resultRepository;

    

    public Optional<Season> getCurrentSeason() {

        return seasonRepository.findActiveSeason();

    }

    

    public List<SeasonRank> getCurrentSeasonRanking() {

        return seasonRankRepository.findCurrentSeasonRanking();

    }

    

    public void rebuildRanks() {

        Optional<Season> currentSeason = getCurrentSeason();

        if (currentSeason.isEmpty()) return;

        

        Season season = currentSeason.get();

        

        List<SeasonRank> existingRanks = seasonRankRepository.findBySeasonIdOrderByRank(season.getId());

        seasonRankRepository.deleteAll(existingRanks);

        

        List<User> users = userRepository.findTopPlayersByMmr();

        AtomicInteger rank = new AtomicInteger(1);

        

        users.forEach(user -> {

            Long seasonScore = calculateUserSeasonScore(user.getId(), season.getId());

            

            SeasonRank seasonRank = new SeasonRank();

            seasonRank.setSeason(season);

            seasonRank.setUser(user);

            seasonRank.setRankPosition(rank.getAndIncrement());

            seasonRank.setSeasonScore(seasonScore);

            

            seasonRankRepository.save(seasonRank);

        });

    }

    

    private Long calculateUserSeasonScore(Long userId, Long seasonId) {

        return resultRepository.countSuccessfulResults(userId) * 100L;

    }

    

    public void updateUserSeasonScore(Long userId, Integer scoreToAdd) {

        Optional<Season> currentSeason = getCurrentSeason();

        if (currentSeason.isEmpty()) return;

        

        Optional<SeasonRank> seasonRank = seasonRankRepository

            .findBySeasonAndUser(currentSeason.get().getId(), userId);

        

        if (seasonRank.isPresent()) {

            SeasonRank rank = seasonRank.get();

            rank.setSeasonScore(rank.getSeasonScore() + scoreToAdd);

            seasonRankRepository.save(rank);

        } else {

            SeasonRank newRank = new SeasonRank();

            newRank.setSeason(currentSeason.get());

            newRank.setUser(userRepository.findById(userId).orElse(null));

            newRank.setSeasonScore((long) scoreToAdd);

            newRank.setRankPosition(999999);

            seasonRankRepository.save(newRank);

        }

    }

}

