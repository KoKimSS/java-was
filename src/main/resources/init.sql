-- SQL 스크립트: init_tables.sql

-- Member 테이블 삭제 및 생성
-- Article 테이블 삭제 및 생성
DROP TABLE IF EXISTS comment CASCADE;
DROP TABLE IF EXISTS article CASCADE;
DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id VARCHAR(255) NOT NULL,
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL
);



-- Comment 테이블 삭제 및 생성
CREATE TABLE article (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         contents TEXT,
                         user_id BIGINT NOT NULL,
                         FOREIGN KEY (user_id) REFERENCES member(id),
                         file_path VARCHAR(255)
);

CREATE TABLE comment (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         contents TEXT NOT NULL,
                         user_id BIGINT NOT NULL,
                         poster_id BIGINT NOT NULL,
                         FOREIGN KEY (user_id) REFERENCES member(id),
                         FOREIGN KEY (poster_id) REFERENCES article(id)
);

-- Member 데이터 삽입
INSERT INTO member (user_id, username, password) VALUES
                                                     ('test1', 'User One', 'test'),
                                                     ('test2', 'User Two', 'test');

-- Article 데이터 삽입
INSERT INTO article (title, contents, user_id) VALUES
                                                   ('Title 1', 'Content of article 1', 1),
                                                   ('Title 2', 'Content of article 2', 2);

-- Comment 데이터 삽입
INSERT INTO comment (contents, user_id, poster_id) VALUES
                                                       ('This is a comment on article 1 by user 1', 1, 1),
                                                       ('This is another comment on article 1 by user 2', 2, 1),
                                                       ('This is a comment on article 2 by user 1', 1, 2),
                                                       ('This is another comment on article 2 by user 2', 2, 2);
