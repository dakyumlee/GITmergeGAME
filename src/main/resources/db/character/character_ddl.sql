CREATE SEQUENCE character_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE user_character_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE characters (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(50) NOT NULL,
    sprite_emoji VARCHAR2(10),
    special_ability VARCHAR2(100),
    speed_bonus NUMBER DEFAULT 0,
    accuracy_bonus NUMBER DEFAULT 0,
    specialty_tag VARCHAR2(20)
);

CREATE TABLE user_characters (
    id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    character_id NUMBER NOT NULL,
    is_unlocked NUMBER(1) DEFAULT 0,
    is_selected NUMBER(1) DEFAULT 0,
    experience_points NUMBER DEFAULT 0,
    FOREIGN KEY (character_id) REFERENCES characters(id)
);
