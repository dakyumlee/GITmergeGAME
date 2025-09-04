package com.gitmerge.service;

import com.gitmerge.entity.User;
import com.gitmerge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    
    public User register(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalArgumentException("Nickname already exists");
        }
        
        User user = new User();
        user.setNickname(nickname);
        return userRepository.save(user);
    }
    
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }
    
    public void updateMmr(Long userId, Integer newMmr) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setMmr(newMmr);
        userRepository.save(user);
    }
    
    public void addWin(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setWins(user.getWins() + 1);
        userRepository.save(user);
    }
    
    public void addLoss(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setLosses(user.getLosses() + 1);
        userRepository.save(user);
    }
}
