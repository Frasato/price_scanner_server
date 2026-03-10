package org.mege;

import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    static void main() throws IOException {
        ServerSocket server = new ServerSocket(6500);
        System.out.println("Servidor aguardando conexões na porta 6500...");

        while (true) {
            Socket socket = server.accept();
            System.out.println("Terminal conectado: " + socket.getInetAddress());

            Thread t = new Thread(new TerminalHandler(socket));
            t.start();
        }
    }
}

record TerminalHandler(Socket socket) implements Runnable {
    @Override
    public void run() {
        System.out.println("Cuidando do terminal: " + socket.getInetAddress());

        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            String message = "#ok\r\n";
            outputStream.write(message.getBytes());
            outputStream.flush();
            System.out.println("Enviei #ok para o terminal.");

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String raw = new String(buffer, 0, bytesRead);
                System.out.println("Recebido (raw): " + raw);
                System.out.println("Bytes: " + java.util.Arrays.toString(java.util.Arrays.copyOf(buffer, bytesRead)));

                if(!raw.startsWith("#TC")){
                    Product product = new Product();
                    outputStream.write(product.RequestProduct(raw));
                    outputStream.flush();
                }
            }

        } catch (Exception exception) {
            System.out.println("Terminal desconectado: " + exception.getMessage());
        }
    }
}

class Product{
    public byte[] RequestProduct(String barcode) throws IOException, InterruptedException {
        ByteArrayOutputStream message = new ByteArrayOutputStream();
        String newBarcode = barcode.replace("#", "");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:3004/precos/" + newBarcode))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        JSONObject json = new JSONObject(response.body());
        String productName = json.getString("produto");
        double sellPrice = json.getDouble("precovenda");
        Double offerPrice = json.isNull("precooferta") ? null : json.getDouble("precooferta");
        String price;

        if (offerPrice != null) {
            price = "R$" + offerPrice;
        } else {
            price = "R$" + sellPrice;
        }

        byte[] l1Byte = productName.getBytes();
        byte[] l2Byte = price.getBytes();

        int time = 10;

        message.write("#mesg".getBytes());
        message.write(l1Byte.length + 48);
        message.write(l1Byte);
        message.write(l2Byte.length + 48);
        message.write(l2Byte);
        message.write(time + 48);
        message.write(48);

        return message.toByteArray();
    }
}