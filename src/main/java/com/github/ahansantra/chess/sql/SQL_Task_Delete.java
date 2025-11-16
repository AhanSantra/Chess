package com.github.ahansantra.chess.sql;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SQL_Task_Delete {
    static final String BASE_URL = "https://cf-supa-api.chessahansantra.workers.dev/";
    private static final String PASSWORD = "741@Ahan";
    private static final HttpClient client = HttpClient.newHttpClient();

    // Delete a file
    public static void deleteFile(String remoteFileName) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + remoteFileName + "?pass=" + PASSWORD))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Delete Response: " + response.body());
    }
}
