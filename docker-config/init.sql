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

CREATE TABLE datasets (
    id INT AUTO_INCREMENT PRIMARY KEY,         -- Identificatore univoco per ogni dataset
    name VARCHAR(255) NOT NULL,                 -- Nome del dataset
    description TEXT,                           -- Descrizione opzionale del dataset
    file_format VARCHAR(50),                    -- Formato del file (es: CSV, JSON, Excel)
    size BIGINT,                                -- Dimensione del file (in byte)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data di creazione del dataset
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Data dell'ultimo aggiornamento
    data BLOB,                                  -- Il contenuto del dataset (in formato binario, es: CSV, JSON, Excel)
    created_by VARCHAR(255),                    -- Utente che ha caricato il dataset
    tags VARCHAR(255),                          -- Parole chiave per il dataset (opzionale, per facilitare la ricerca)
    status VARCHAR(50) DEFAULT 'active'         -- Stato del dataset (attivo, archiviato, ecc.)
);
