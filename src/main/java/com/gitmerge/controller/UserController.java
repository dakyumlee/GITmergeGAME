package com.gitmerge.controller;

import com.gitmerge.entity.User;
import com.gitmerge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestParam String nickname) {
        User user = userService.register(nickname);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{nickname}")
    public ResponseEntity<User> getUser(@PathVariable String nickname) {
        return userService.findByNickname(nickname)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
