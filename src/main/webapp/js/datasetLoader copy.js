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
    const createDOMElement = (data) => {
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

        cardBody.appendChild(title);
        cardBody.appendChild(description);
        cardBody.appendChild(details);
        cardBody.appendChild(timestamps);
        container.appendChild(cardBody);

        return container;
    };

    // Funzione per caricare i dati dal server
    const loadDatasets = async (path) => {
        try {
            showLoadingIndicator(); // Mostra il pallino di caricamento

            // Ottieni il path corrente e crea l'URL della rotta
            const currentPath = window.location.pathname;
            const url = `${path}/Dataset`;

            // Esegui la richiesta alla rotta
            const response = await fetch(url);
            if (!response.ok) throw new Error("Errore nel caricamento dei dati");

            const data = await response.json();

            // Svuota il contenuto del sidebar e popola con i dati
            sidebar.innerHTML = "";
            data.forEach(item => {
                const domElement = createDOMElement(item);
                sidebar.appendChild(domElement);
            });

            datasetsLoaded = true; // Imposta il flag a true
        } catch (error) {
            console.error("Errore:", error);
            sidebar.innerHTML = "<p class='text-danger'>Errore nel caricamento dei dati.</p>";
        }
    };

