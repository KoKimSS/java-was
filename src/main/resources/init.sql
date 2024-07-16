-- SQL 스크립트: init_tables.sql


-- Member 테이블 삭제 및 생성
DROP TABLE member cascade constraints;
CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id VARCHAR(255) NOT NULL,
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL
);

-- Article 테이블 삭제 및 생성
DROP TABLE article cascade constraints;
CREATE TABLE article (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         contents TEXT,
                         user_id BIGINT NOT NULL,
                         FOREIGN KEY (user_id) REFERENCES member(id)
);

-- Member 데이터 삽입
INSERT INTO member (user_id, username, password) VALUES
                                                     ('user1', 'User One', 'password1'),
                                                     ('user2', 'User Two', 'password2');

-- Article 데이터 삽입
INSERT INTO article (title, contents, user_id) VALUES
                                                   ('Title 1', 'Content of article 1', 1),
                                                   ('Title 2', 'Content of article 2', 2);
