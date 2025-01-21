USE MapDB;

CREATE TABLE Examples(
    id INT AUTO_INCREMENT PRIMARY KEY,
    v1 FLOAT,
    v2 FLOAT,
    v3 FLOAT
);

INSERT INTO Examples (v1, v2, v3)
VALUES 
(1.0, 2.0, 0.0),
(0.0, 1.0,-1.0),
(1.0, 3.0, 5.0),
(1.0, 3.0, 4.0),
(2.0, 2.0, 0.0);

CREATE TABLE Datasets (
    id INT AUTO_INCREMENT PRIMARY KEY,         -- Identificatore univoco per ogni dataset
    name VARCHAR(255) NOT NULL unique,         -- Nome del dataset
    description TEXT,                          -- Descrizione opzionale del dataset
    size BIGINT,                               -- Dimensione della stringa json(in byte)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data di creazione del dataset
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Data dell'ultimo aggiornamento
    type VARCHAR(100),                           -- Classe Nativa del dataset che estende ClusterableItem(es: <Example>,<Image>,..)
    data BLOB not NULL,                          -- Il contenuto del dataset (JSON binary format)
    tags VARCHAR(255)                            -- Parole chiave per il dataset (opzionale, per facilitare la ricerca)
);