package com.github.ahansantra.chess.sql;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class SQL_Task_Download {
    static final String BASE_URL = "https://cf-supa-api.chessahansantra.workers.dev/";
    private static final String PASSWORD = "741@Ahan";

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void downloadFile(String remoteFileName, String saveAs) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + remoteFileName + "?pass=" + PASSWORD))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            Files.write(Path.of(saveAs), response.body());
            System.out.println("File downloaded as " + saveAs);
        } else {
            System.out.println("Download failed: " + new String(response.body()));
        }
    }

}

