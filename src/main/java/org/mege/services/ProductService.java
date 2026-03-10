package org.mege.services;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProductService {
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
