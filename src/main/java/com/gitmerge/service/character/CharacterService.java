package com.gitmerge.service.character;

import com.gitmerge.entity.character.Character;
import com.gitmerge.entity.character.UserCharacter;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class CharacterService {
    
    @PersistenceContext
    private EntityManager em;
    
    public List<Character> getAllCharacters() {
        return em.createQuery("SELECT c FROM Character c", Character.class).getResultList();
    }
    
    public List<UserCharacter> getUserCharacters(Long userId) {
        return em.createQuery("SELECT uc FROM UserCharacter uc WHERE uc.userId = :userId", UserCharacter.class)
                .setParameter("userId", userId).getResultList();
    }
    
    public Character getSelectedCharacter(Long userId) {
        List<UserCharacter> userChars = em.createQuery(
            "SELECT uc FROM UserCharacter uc WHERE uc.userId = :userId AND uc.isSelected = true", 
            UserCharacter.class)
            .setParameter("userId", userId).getResultList();
        
        if (!userChars.isEmpty()) {
            Long characterId = userChars.get(0).getCharacterId();
            return em.find(Character.class, characterId);
        }
        
        return em.find(Character.class, 1L);
    }
    
    public void selectCharacter(Long userId, Long characterId) {
        em.createQuery("UPDATE UserCharacter uc SET uc.isSelected = false WHERE uc.userId = :userId")
            .setParameter("userId", userId).executeUpdate();
        
        em.createQuery("UPDATE UserCharacter uc SET uc.isSelected = true WHERE uc.userId = :userId AND uc.characterId = :characterId")
            .setParameter("userId", userId)
            .setParameter("characterId", characterId).executeUpdate();
    }
    
    public void unlockCharacter(Long userId, Long characterId) {
        UserCharacter uc = new UserCharacter();
        uc.setUserId(userId);
        uc.setCharacterId(characterId);
        uc.setIsUnlocked(true);
        uc.setIsSelected(false);
        em.persist(uc);
    }
}
