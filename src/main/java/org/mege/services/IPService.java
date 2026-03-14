package org.mege.services;

import java.net.URI;
import java.net.Socket;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class IPService {

    static HttpClient client = HttpClient.newHttpClient();

    public IPService(Socket socket) {
        try {

            String ip = socket.getInetAddress().getHostAddress();

            String json = "{\"ip\":\"" + ip + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:3004/ip"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}