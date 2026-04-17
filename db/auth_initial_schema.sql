CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE TABLE users (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       password VARCHAR(60) NOT NULL,
       email VARCHAR(255) UNIQUE,
       phone_number VARCHAR(255) NOT NULL UNIQUE,
       first_name VARCHAR(255) NOT NULL,
       last_name VARCHAR(255) NOT NULL,
       middle_name VARCHAR(255),
       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP,
       role VARCHAR(50) NOT NULL,
        is_verificated BOOLEAN NOT NULL
);

CREATE TABLE jwt (
         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
         token TEXT NOT NULL,
         user_id UUID NOT NULL,
         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
         updated_at TIMESTAMP,
         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE verification(
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        email VARCHAR(255) NOT NULL UNIQUE,
        verification_code VARCHAR(10) NOT NULL,
        FOREIGN KEY (email) REFERENCES users(email) ON DELETE CASCADE
);

CREATE INDEX idx_jwt_user_id ON jwt(user_id);
CREATE INDEX idx_verification_user_email ON verification(email);