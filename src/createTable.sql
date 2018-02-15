CREATE TABLE profil(
                    cid INT,
                    gender INT,
                    lang VARCHAR (10),
                    data_user INT,
                    name VARCHAR (100),
                    friends INT,
                    label VARCHAR(100) PRIMARY KEY,
                    visited INT DEFAULT 0);

ALTER TABLE profil CONVERT TO CHARACTER SET utf8;
