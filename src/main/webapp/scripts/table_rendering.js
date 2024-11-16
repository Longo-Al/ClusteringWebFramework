$(document).ready(function() {
    $('#addColumn').click(function() {
        $('#columnsContainer').append(`
            <div class="row mb-2 column-row">
                <div class="col">
                    <input type="text" class="form-control" name="columnName" placeholder="Nome colonna" required>
                </div>
                <div class="col">
                    <input type="text" class="form-control" name="columnType" placeholder="Tipo colonna" required>
                </div>
                <div class="col-auto">
                    <button type="button" class="btn btn-danger remove-column">Rimuovi</button>
                </div>
            </div>
        `);
    });

    $(document).on('click', '.remove-column', function() {
        $(this).closest('.column-row').remove();
    });

    $('#createTableForm').on('submit', function(event) {
        event.preventDefault();

        var tableName = $('#tableName').val();
        var columns = [];

        $('#columnsContainer .column-row').each(function() {
            var columnName = $(this).find('input[name="columnName"]').val();
            var columnType = $(this).find('input[name="columnType"]').val();
            columns.push({ name: columnName, type: columnType });
        });

        var tableData = {
            tableName: tableName,
            columns: columns
        };

        // Stampa il JSON nell'elemento HTML per debug
        $('#jsonOutput').text(JSON.stringify(tableData, null, 2));

        // Opzionale: invio dei dati al server
        /*
        $.ajax({
            url: '/create_table',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(tableData),
            success: function(response) {
                alert('Tabella creata con successo: ' + response.message);
            },
            error: function(error) {
                alert('Errore nella creazione della tabella: ' + error.responseJSON.message);
            }
        });
        */
    });
});
