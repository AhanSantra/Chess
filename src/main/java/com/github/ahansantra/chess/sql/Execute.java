package com.github.ahansantra.chess.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.ahansantra.chess.sql.SQL_Task_Delete.deleteFile;
import static com.github.ahansantra.chess.sql.SQL_Task_Download.downloadFile;
import static com.github.ahansantra.chess.sql.SQL_Task_New.uploadFile;

public class Execute {

    // Path to your downloaded .sqlite file
    private static final String DB_FILE = "downloaded_database.sqlite";

    public static void executeQuery(String sql) throws SqlException {
        String url = "jdbc:sqlite:" + DB_FILE;

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            if (sql.trim().toUpperCase().startsWith("SELECT")) {
                ResultSet rs = stmt.executeQuery(sql);
                int cols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= cols; i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }
            } else {
                int affected = stmt.executeUpdate(sql);
                System.out.println("Rows affected: " + affected);
            }

        } catch (Exception e) {
            throw new SqlException("crap");
        }
    }

    // Example main
    public static void main(String[] args) throws Exception {
        // Download sqlite file from Worker
        downloadFile("mydb.sqlite", DB_FILE);

        // Execute SQL commands directly
        executeQuery("CREATE TABLE IF NOT EXISTS test_table(id INTEGER PRIMARY KEY, name TEXT);");
        executeQuery("INSERT INTO test_table(name) VALUES('Alice');");
        executeQuery("SELECT * FROM test_table;");

        // Delete and re-upload file
        deleteFile("mydb.sqlite");
        uploadFile(String.valueOf(Path.of("mydb.sqlite")), DB_FILE);

        // Delete local copy
        Files.delete(Path.of("mydb.sqlite"));
    }
}
