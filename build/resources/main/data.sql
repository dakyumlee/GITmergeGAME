-- 캐릭터 기본 데이터
INSERT INTO characters (name, sprite_emoji, special_ability, speed_bonus, accuracy_bonus, specialty_tag) VALUES
('신입개발자', '👶', '실수되돌리기', 0, 0, 'beginner'),
('프론트엔드', '🎨', 'CSS충돌자동해결', 10, 5, 'frontend'),
('백엔드', '🛠️', 'DB스키마힌트', 5, 15, 'backend'),
('DevOps', '⚙️', '충돌미리보기', 20, 10, 'devops'),
('시니어', '👨‍💻', '자동코드정렬', 15, 20, 'senior')
ON CONFLICT DO NOTHING;

-- 업적 기본 데이터
INSERT INTO achievements (code, achievement_name, description, points) VALUES
('FIRST_MERGE', '첫 머지', '첫 번째 충돌을 성공적으로 해결했습니다', 10),
('SPEED_DEMON', '스피드 데몬', '60초 내에 게임을 완료했습니다', 25),
('PERFECTIONIST', '완벽주의자', '95% 이상의 정확도를 달성했습니다', 30),
('MERGE_KING', '머지의 제왕', '2000점 이상을 획득했습니다', 50),
('REBASE_SURVIVOR', '리베이스 생존자', 'HELL 난이도를 완료했습니다', 100)
ON CONFLICT DO NOTHING;
