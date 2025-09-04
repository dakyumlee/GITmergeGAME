package com.gitmerge.entity.character;

import jakarta.persistence.*;

@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "sprite_emoji")
    private String spriteEmoji;
    
    @Column(name = "special_ability")
    private String specialAbility;
    
    @Column(name = "speed_bonus")
    private Integer speedBonus = 0;
    
    @Column(name = "accuracy_bonus")
    private Integer accuracyBonus = 0;
    
    @Column(name = "specialty_tag")
    private String specialtyTag;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpriteEmoji() { return spriteEmoji; }
    public void setSpriteEmoji(String spriteEmoji) { this.spriteEmoji = spriteEmoji; }
    public String getSpecialAbility() { return specialAbility; }
    public void setSpecialAbility(String specialAbility) { this.specialAbility = specialAbility; }
    public Integer getSpeedBonus() { return speedBonus; }
    public void setSpeedBonus(Integer speedBonus) { this.speedBonus = speedBonus; }
    public Integer getAccuracyBonus() { return accuracyBonus; }
    public void setAccuracyBonus(Integer accuracyBonus) { this.accuracyBonus = accuracyBonus; }
    public String getSpecialtyTag() { return specialtyTag; }
    public void setSpecialtyTag(String specialtyTag) { this.specialtyTag = specialtyTag; }
}
