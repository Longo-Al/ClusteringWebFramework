package Map.Server.src.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Map.Server.src.database.Exception.DatabaseConnectionException;

public class TableSchema implements Iterable<String> {
    private final DbAccess db;
	private final String table_Name; 

    public static class Column {
        private final String name;
        private final String type;

        Column(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getColumnName() {
            return name;
        }

        public boolean isNumber() {
            return type.equals("number");
        }

        public String toString() {
            return name + ":" + type;
        }
    }

    List<Column> tableSchema = new ArrayList<>();

    public TableSchema(DbAccess db, String tableName) throws SQLException, DatabaseConnectionException {
        this.db = db;
		this.table_Name = tableName;
        HashMap<String, String> mapSQL_JAVATypes = new HashMap<>();
        mapSQL_JAVATypes.put("CHAR", "string");
        mapSQL_JAVATypes.put("VARCHAR", "string");
        mapSQL_JAVATypes.put("LONGVARCHAR", "string");
        mapSQL_JAVATypes.put("BIT", "string");
        mapSQL_JAVATypes.put("SHORT", "number");
        mapSQL_JAVATypes.put("INT", "number");
        mapSQL_JAVATypes.put("LONG", "number");
        mapSQL_JAVATypes.put("FLOAT", "number");
        mapSQL_JAVATypes.put("DOUBLE", "number");
        mapSQL_JAVATypes.put("BLOB", "Blob");

        Connection con = db.getConnection();
        DatabaseMetaData meta = con.getMetaData();
        ResultSet res = meta.getColumns(null, null, tableName, null);

        while (res.next()) {
            if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME"))) {
                tableSchema.add(new Column(
                        res.getString("COLUMN_NAME"),
                        mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))
                ));
            }
        }
        res.close();
    }

    public int getNumberOfAttributes() {
        return tableSchema.size();
    }

    public Column getColumn(int index) {
        return tableSchema.get(index);
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < tableSchema.size();
            }

            @Override
            public String next() {
                return tableSchema.get(currentIndex++).getColumnName();
            }
        };
    }

    /**
     * Crea e prepara una query di inserimento basata sullo schema della tabella.
     * 
     * @param values Una lista di valori da inserire (nell'ordine delle colonne).
     * @return La query SQL di tipo INSERT INTO.
     * @throws SQLException Se si verifica un errore durante la preparazione della query.
     */
    public String prepareInsertQuery(List<String> values) throws SQLException {
        if (values.size() != tableSchema.size()) {
            throw new SQLException("Il numero di valori non corrisponde al numero di colonne.");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + this.table_Name + "(");
        StringBuilder placeholders = new StringBuilder();

        // Crea la lista delle colonne e i relativi segnaposti per i valori
        for (int i = 0; i < tableSchema.size(); i++) {
            Column column = tableSchema.get(i);
            sql.append(column.getColumnName());
            placeholders.append("?");	
            if (i < tableSchema.size() - 1) {
                sql.append(", ");
                placeholders.append(", ");
            }

        }

        sql.append(") VALUES (").append(placeholders).append(")");
        return sql.toString();
    }

    /**
     * Esegue l'inserimento dei valori nella tabella.
     * 
     * @param values Una lista di valori da inserire (nell'ordine delle colonne).
     * @throws SQLException Se si verifica un errore durante l'inserimento.
		  * @throws DatabaseConnectionException 
		  */
		public void insertIntoTable(List<String> values) throws SQLException, DatabaseConnectionException {
        	String sql = prepareInsertQuery(values);

			Connection con = db.getConnection();
        	PreparedStatement pstmt = con.prepareStatement(sql);

            // Imposta i parametri del PreparedStatement
            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            }

            pstmt.executeUpdate();
        }
}
