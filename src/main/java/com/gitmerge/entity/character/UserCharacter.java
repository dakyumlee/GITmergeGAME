package com.gitmerge.entity.character;

import jakarta.persistence.*;

@Entity
@Table(name = "user_characters")
@SequenceGenerator(name = "user_character_seq", sequenceName = "user_character_seq", allocationSize = 1)
public class UserCharacter {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_character_seq")
   private Long id;
   
   @Column(name = "user_id", nullable = false)
   private Long userId;
   
   @Column(name = "character_id", nullable = false)
   private Long characterId;
   
   @Column(name = "is_unlocked")
   private Boolean isUnlocked = false;
   
   @Column(name = "is_selected")
   private Boolean isSelected = false;
   
   @Column(name = "experience_points")
   private Integer experiencePoints = 0;
   
   public Long getId() { return id; }
   public void setId(Long id) { this.id = id; }
   public Long getUserId() { return userId; }
   public void setUserId(Long userId) { this.userId = userId; }
   public Long getCharacterId() { return characterId; }
   public void setCharacterId(Long characterId) { this.characterId = characterId; }
   public Boolean getIsUnlocked() { return isUnlocked; }
   public void setIsUnlocked(Boolean isUnlocked) { this.isUnlocked = isUnlocked; }
   public Boolean getIsSelected() { return isSelected; }
   public void setIsSelected(Boolean isSelected) { this.isSelected = isSelected; }
   public Integer getExperiencePoints() { return experiencePoints; }
   public void setExperiencePoints(Integer experiencePoints) { this.experiencePoints = experiencePoints; }
}
