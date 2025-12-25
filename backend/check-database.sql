-- 데이터베이스 및 테이블 확인 스크립트

-- 1. 데이터베이스 존재 확인
SHOW DATABASES LIKE 'board';

-- 2. 데이터베이스 선택
USE board;

-- 3. 테이블 목록 확인
SHOW TABLES;

-- 4. User 테이블 구조 확인
DESCRIBE user;

-- 5. Post 테이블 구조 확인
DESCRIBE post;

-- 6. RefreshToken 테이블 구조 확인
DESCRIBE refresh_token;

-- 7. 기존 사용자 데이터 확인
SELECT * FROM user;

-- 8. 기존 토큰 데이터 확인
SELECT * FROM refresh_token;
