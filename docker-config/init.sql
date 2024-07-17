USE MapDB;

CREATE TABLE examples(
    id INT AUTO_INCREMENT PRIMARY KEY,
    v1 FLOAT,
    v2 FLOAT,
    v3 FLOAT
);

INSERT INTO examples (v1, v2, v3)
VALUES 
(1.0, 2.0, 0.0),
(0.0, 1.0,-1.0),
(1.0, 3.0, 5.0),
(1.0, 3.0, 4.0),
(2.0, 2.0, 0.0);