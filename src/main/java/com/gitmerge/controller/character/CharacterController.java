package com.gitmerge.controller.character;

import com.gitmerge.entity.character.Character;
import com.gitmerge.entity.character.UserCharacter;
import com.gitmerge.service.character.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/character")
public class CharacterController {
    
    @Autowired
    private CharacterService characterService;
    
    @GetMapping("/all")
    public List<Character> getAllCharacters() {
        return characterService.getAllCharacters();
    }
    
    @GetMapping("/user/{userId}")
    public List<UserCharacter> getUserCharacters(@PathVariable Long userId) {
        return characterService.getUserCharacters(userId);
    }
    
    @GetMapping("/selected/{userId}")
    public Character getSelectedCharacter(@PathVariable Long userId) {
        return characterService.getSelectedCharacter(userId);
    }
    
    @PostMapping("/select")
    public Map<String, Object> selectCharacter(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long characterId = request.get("characterId");
        characterService.selectCharacter(userId, characterId);
        return Map.of("success", true, "message", "캐릭터 선택 완료");
    }
    
    @PostMapping("/unlock")
    public Map<String, Object> unlockCharacter(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long characterId = request.get("characterId");
        characterService.unlockCharacter(userId, characterId);
        return Map.of("success", true, "message", "캐릭터 해제 완료");
    }
}
