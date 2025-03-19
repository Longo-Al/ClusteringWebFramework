// Seleziona il contenitore della sidebar e imposta il flag di caricamento
const sidebar = document.querySelector("#sidebar");
let datasetsLoaded = false; // Flag per evitare caricamenti multipli

// Funzione per mostrare il pallino di caricamento
const showLoadingIndicator = () => {
    sidebar.innerHTML = `
        <div class="d-flex justify-content-center align-items-center h-100">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;
};

// Funzione per creare un elemento DOM con i dati del JSON
// Funzione per creare un elemento DOM con i dati del JSON
const createDOMElement = (data, basePath) => {
    const container = document.createElement("div");
    container.className = "card mb-3";

    const cardBody = document.createElement("div");
    cardBody.className = "card-body";

    const title = document.createElement("h5");
    title.className = "card-title";
    title.textContent = data.name;

    const description = document.createElement("p");
    description.className = "card-text";
    description.textContent = data.description;

    const details = document.createElement("p");
    details.className = "card-text";
    details.innerHTML = `
        <strong>Size:</strong> ${data.size} bytes<br>
        <strong>Type:</strong> ${data.type}<br>
        <strong>Tags:</strong> ${data.tags || "No tags"}
    `;

    const timestamps = document.createElement("p");
    timestamps.className = "card-text";
    timestamps.innerHTML = `
        <small class="text-muted">
            Created At: ${new Date(data.created_at).toLocaleString()}<br>
            Updated At: ${new Date(data.updated_at).toLocaleString()}
        </small>
    `;

    // Crea il pulsante per caricare i dettagli del dataset
    const loadButton = document.createElement("button");
    loadButton.className = "btn btn-primary";
    loadButton.textContent = "Clusterize";
    loadButton.onclick = () => {
        loadDatasetDetails(basePath, data.id);
    };

    cardBody.appendChild(title);
    cardBody.appendChild(description);
    cardBody.appendChild(details);
    cardBody.appendChild(timestamps);
    cardBody.appendChild(loadButton);
    container.appendChild(cardBody);

    return container;
};


// Funzione per caricare i dati dal server
const loadDatasets = async (path) => {
    try {
        showLoadingIndicator(); // Mostra il pallino di caricamento

        // Crea l'URL della rotta basandoti sul percorso fornito
        const url = `${path}/Dataset`;

        // Esegui la richiesta alla rotta
        const response = await fetch(url);
        if (!response.ok) throw new Error("Errore nel caricamento dei dati");

        const data = await response.json();

        // Svuota il contenuto del sidebar e popola con i dati
        sidebar.innerHTML = "";
        data.forEach(item => {
            const domElement = createDOMElement(item, path);
            sidebar.appendChild(domElement);
        });

        datasetsLoaded = true; // Imposta il flag a true
    } catch (error) {
        console.error("Errore:", error);
        sidebar.innerHTML = "<p class='text-danger'>Errore nel caricamento dei dati.</p>";
    }
};

const loadDatasetDetails = async (basePath, id) => {
    try {
        // Mostra un indicatore di caricamento durante il fetch
        sidebar.innerHTML = `
            <div class="d-flex justify-content-center align-items-center h-100">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
        `;

        // URL dinamico per caricare il dataset specifico
        const url = `${basePath}/Dataset/${id}`;
        console.log(`Fetching dataset details from: ${url}`);

        // Effettua la richiesta
        const response = await fetch(url);

        // Controlla lo stato della risposta
        if (!response.ok) {
            console.error(`Errore nella risposta: ${response.status} ${response.statusText}`);
            throw new Error("Errore nel caricamento dei dettagli del dataset");
        }

        // Prova a leggere i dati JSON
        const data = await response.json();
        console.log("Dati ricevuti:", data);

        // Verifica che il dataset sia valido
        if (!data || !data.name) {
            console.error("Dati non validi ricevuti:", data);
            throw new Error("Il dataset ricevuto non è valido");
        }

        // Mostra i dettagli del dataset
        sidebar.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">${data.name}</h5>
                    <p class="card-text">${data.description}</p>
                    <p class="card-text"><strong>Size:</strong> ${data.size} bytes</p>
                    <p class="card-text"><strong>Type:</strong> ${data.type}</p>
                    <p class="card-text"><strong>Tags:</strong> ${data.tags || "No tags"}</p>
                    <p class="card-text">
                        <small class="text-muted">
                            Created At: ${new Date(data.created_at).toLocaleString()}<br>
                            Updated At: ${new Date(data.updated_at).toLocaleString()}
                        </small>
                    </p>
                </div>
            </div>
        `;
    } catch (error) {
        console.error("Errore durante il caricamento dei dettagli del dataset:", error);
        sidebar.innerHTML = "<p class='text-danger'>Errore nel caricamento dei dettagli del dataset.</p>";
    }
};
