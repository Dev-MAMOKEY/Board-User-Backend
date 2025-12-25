-- 데이터베이스 생성 (이미 존재하면 스킵)
CREATE DATABASE IF NOT EXISTS board CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE board;

-- 기존 테이블 삭제 (순서 중요: FK 관계 때문에 자식 먼저 삭제)
DROP TABLE IF EXISTS refresh_token;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS user;

-- User 테이블 생성
CREATE TABLE user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider VARCHAR(20) NOT NULL,
    password VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    sns_id VARCHAR(100) UNIQUE,
    login_id VARCHAR(50) UNIQUE,
    email VARCHAR(255) UNIQUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Post 테이블 생성
CREATE TABLE post (
    post_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- RefreshToken 테이블 생성
CREATE TABLE refresh_token (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    refresh_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_user_sns_id ON user(sns_id);
CREATE INDEX idx_user_email ON user(email);
CREATE INDEX idx_user_login_id ON user(login_id);
CREATE INDEX idx_post_user_id ON post(user_id);
CREATE INDEX idx_post_is_deleted ON post(is_deleted);
CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX idx_refresh_token_token ON refresh_token(refresh_token);
CREATE INDEX idx_refresh_token_is_revoked ON refresh_token(is_revoked);

-- 테이블 생성 확인
SHOW TABLES;

SELECT 'Database setup completed successfully!' as message;
