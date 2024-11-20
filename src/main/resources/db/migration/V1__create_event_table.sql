CREATE TABLE events
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255)                        NOT NULL,
    start_time TIMESTAMP                           NOT NULL,
    end_time   TIMESTAMP                           NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);