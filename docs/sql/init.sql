CREATE DATABASE IF NOT EXISTS task_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE task_manager;

CREATE TABLE IF NOT EXISTS task (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    created_time DATETIME,
    updated_time DATETIME
    );
