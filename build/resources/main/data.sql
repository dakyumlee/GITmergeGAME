-- 사용자 테이블 샘플 데이터
INSERT INTO users (nickname, mmr, total_score, wins, losses) VALUES 
('Player1', 1000, 0, 0, 0),
('Player2', 1200, 150, 3, 1);

-- 게임 테이블 샘플 데이터  
INSERT INTO games (seed, difficulty, game_mode, conflict_pack) VALUES
('daily_20240904', 'EASY', 'SOLO', '{"conflicts": [{"type": "DEFAULT", "complexity": 3}]}'),
('daily_20240904', 'NORMAL', 'SOLO', '{"conflicts": [{"type": "DEFAULT", "complexity": 6}]}');
