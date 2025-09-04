INSERT INTO characters (id, name, sprite_emoji, special_ability, speed_bonus, accuracy_bonus, specialty_tag) VALUES
(character_seq.NEXTVAL, '신입개발자', '👶', '실수되돌리기', 0, 0, 'beginner');

INSERT INTO characters (id, name, sprite_emoji, special_ability, speed_bonus, accuracy_bonus, specialty_tag) VALUES
(character_seq.NEXTVAL, '프론트엔드', '🎨', 'CSS충돌자동해결', 10, 5, 'frontend');

INSERT INTO characters (id, name, sprite_emoji, special_ability, speed_bonus, accuracy_bonus, specialty_tag) VALUES
(character_seq.NEXTVAL, '백엔드', '🛠️', 'DB스키마힌트', 5, 15, 'backend');

INSERT INTO characters (id, name, sprite_emoji, special_ability, speed_bonus, accuracy_bonus, specialty_tag) VALUES
(character_seq.NEXTVAL, 'DevOps', '⚙️', '충돌미리보기', 20, 10, 'devops');

INSERT INTO characters (id, name, sprite_emoji, special_ability, speed_bonus, accuracy_bonus, specialty_tag) VALUES
(character_seq.NEXTVAL, '시니어', '👨‍💻', '자동코드정렬', 15, 20, 'senior');

INSERT INTO user_characters (id, user_id, character_id, is_unlocked, is_selected) VALUES
(user_character_seq.NEXTVAL, 1, 1, 1, 1);

COMMIT;
