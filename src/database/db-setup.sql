-- Drop tables if they exist
DROP TABLE IF EXISTS media_content, media, users;

-- Table to store users
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password BYTEA NOT NULL,
    shared_symmetric_key VARCHAR(255) NOT NULL,
    family_symmetric_key VARCHAR(255) NOT NULL,
    UNIQUE(shared_symmetric_key,family_symmetric_key),
    PRIMARY KEY (user_id)
);

-- Table to store media information
CREATE TABLE media (
    media_id SERIAL PRIMARY KEY,
    owner_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    format VARCHAR(50) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL
);

-- Table to store media content
CREATE TABLE media_content (
    media_id INT REFERENCES media(media_id) ON DELETE CASCADE,
    lyrics TEXT NOT NULL,
    audio_base64 TEXT NOT NULL,
    PRIMARY KEY (media_id)
);
