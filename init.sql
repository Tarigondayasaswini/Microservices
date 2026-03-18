-- Drop databases if they exist to ensure a clean start
DROP DATABASE IF EXISTS revplay_users;
DROP DATABASE IF EXISTS revplay_catalog;
DROP DATABASE IF EXISTS revplay_playlists;
DROP DATABASE IF EXISTS revplay_playback;
DROP DATABASE IF EXISTS revplay_analytics;

-- Create all required databases
CREATE DATABASE revplay_users;
CREATE DATABASE revplay_catalog;
CREATE DATABASE revplay_playlists;
CREATE DATABASE revplay_playback;
CREATE DATABASE revplay_analytics;

-- Grant privileges
CREATE USER IF NOT EXISTS 'revplay_user'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON revplay_users.* TO 'revplay_user'@'%';
GRANT ALL PRIVILEGES ON revplay_catalog.* TO 'revplay_user'@'%';
GRANT ALL PRIVILEGES ON revplay_playlists.* TO 'revplay_user'@'%';
GRANT ALL PRIVILEGES ON revplay_playback.* TO 'revplay_user'@'%';
GRANT ALL PRIVILEGES ON revplay_analytics.* TO 'revplay_user'@'%';
FLUSH PRIVILEGES;

-- ==========================================
-- 1. revplay_users SCHEMA
-- ==========================================
USE revplay_users;

CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(16) NOT NULL DEFAULT 'LISTENER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email_otp VARCHAR(20),
    otp_expiry_time DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE user_profiles (
    profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(255),
    bio VARCHAR(1000),
    profile_picture_url VARCHAR(500),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Basic identifiers for testing login (Admin and Test User)
INSERT INTO users (user_id, username, email, password_hash, role) VALUES 
(1, 'admin', 'admin@revplay.com', '$2a$10$89vH3Fayk3/rshgGq8Hthe9G9p3eO6Qp5Q1.6iE9Xj8z1vE9Xj8z1', 'ADMIN'),
(6, 'testuser', 'user@test.com', '$2a$10$89vH3Fayk3/rshgGq8Hthe9G9p3eO6Qp5Q1.6iE9Xj8z1vE9Xj8z1', 'LISTENER');

INSERT INTO user_profiles (user_id, full_name, bio) VALUES 
(1, 'Admin', 'RevPlay Administrator'),
(6, 'Test User', 'Standard listener account');

-- ==========================================
-- 2. revplay_catalog SCHEMA
-- ==========================================
USE revplay_catalog;

CREATE TABLE artists (
    artist_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    bio TEXT,
    banner_image_url VARCHAR(1000),
    artist_type VARCHAR(50) DEFAULT 'MUSIC',
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE albums (
    album_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artist_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    cover_art_url VARCHAR(1000),
    release_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id) ON DELETE CASCADE
);

CREATE TABLE songs (
    song_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    artist_id BIGINT NOT NULL,
    album_id BIGINT,
    title VARCHAR(255) NOT NULL,
    duration_seconds INT NOT NULL,
    file_url VARCHAR(1000) NOT NULL,
    visibility VARCHAR(20) DEFAULT 'PUBLIC',
    release_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id) ON DELETE CASCADE,
    FOREIGN KEY (album_id) REFERENCES albums(album_id) ON DELETE SET NULL
);

CREATE TABLE genres (
    genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE song_genres (
    song_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (song_id, genre_id),
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE
);

-- ==========================================
-- 3. revplay_playlists SCHEMA
-- ==========================================
USE revplay_playlists;

CREATE TABLE playlists (
    playlist_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cover_image_url VARCHAR(1000),
    is_public BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE TABLE playlist_songs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    playlist_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    position INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE
);

CREATE TABLE user_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    likeable_id BIGINT NOT NULL,
    likeable_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    UNIQUE KEY idx_user_like (user_id, likeable_id, likeable_type)
);

CREATE TABLE system_playlists (
    system_playlist_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    cover_image_url VARCHAR(1000),
    playlist_type VARCHAR(50) DEFAULT 'FEATURED',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE system_playlist_songs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    system_playlist_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    position INT DEFAULT 0,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (system_playlist_id) REFERENCES system_playlists(system_playlist_id) ON DELETE CASCADE
);

-- ==========================================
-- 4. revplay_playback SCHEMA
-- ==========================================
USE revplay_playback;

CREATE TABLE play_history (
    play_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    song_id BIGINT,
    episode_id BIGINT,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed BOOLEAN DEFAULT FALSE,
    play_duration_seconds INT DEFAULT 0
);

CREATE TABLE queue_items (
    queue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    song_id BIGINT,
    episode_id BIGINT,
    position INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    UNIQUE KEY uk_queue_items_user_position (user_id, position)
);

CREATE TABLE listening_sessions (
    session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_id VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_pushed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 5. revplay_analytics SCHEMA
-- ==========================================
USE revplay_analytics;

CREATE TABLE user_subscriptions (
    subscription_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    plan_type VARCHAR(50) NOT NULL DEFAULT 'PREMIUM',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action_type VARCHAR(100) NOT NULL,
    user_id BIGINT,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ad_campaigns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);
