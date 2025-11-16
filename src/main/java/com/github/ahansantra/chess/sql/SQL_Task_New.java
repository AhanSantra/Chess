package com.github.ahansantra.chess.sql;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class SQL_Task_New {
    static final String BASE_URL = "https://cf-supa-api.chessahansantra.workers.dev/";
    private static final String PASSWORD = "741@Ahan";

    private static final HttpClient client = HttpClient.newHttpClient();

    // Upload a file
    public static void uploadFile(String localPath, String remoteFileName) throws Exception {
        byte[] data = Files.readAllBytes(Path.of(localPath));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + remoteFileName + "?pass=" + PASSWORD))
                .PUT(HttpRequest.BodyPublishers.ofByteArray(data))
                .header("Content-Type", "text/sql")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Upload Response: " + response.body());
    }
}
