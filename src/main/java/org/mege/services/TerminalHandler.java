package org.mege.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public record TerminalHandler(Socket socket) implements Runnable {
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
                    ProductService product = new ProductService();
                    outputStream.write(product.RequestProduct(raw));
                    outputStream.flush();
                }
            }

        } catch (Exception exception) {
            System.out.println("Terminal desconectado: " + exception.getMessage());
        }
    }
}