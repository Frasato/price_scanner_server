package org.mege.services;

import org.mege.ui.ConsoleWindow;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public record TerminalHandler(Socket socket) implements Runnable {
    @Override
    public void run() {
        new IPService(socket);
        System.out.println(socket.getInetAddress() + ":" + socket.getPort() + " Connected");
        ConsoleWindow.getInstance().updateTerminalCount(1);

        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            String message = "#ok\r\n";
            outputStream.write(message.getBytes());
            outputStream.flush();

            LiveThread liveThread = new LiveThread();
            Thread newThread = liveThread.getThread(outputStream, socket);
            newThread.start();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String raw = new String(buffer, 0, bytesRead);
                System.out.println("Terminal Model: " + raw);

                if(!raw.startsWith("#TC") && !raw.startsWith("#live")){
                    ProductService product = new ProductService();
                    outputStream.write(product.RequestProduct(raw));
                    outputStream.flush();
                }
            }

        } catch (Exception exception) {
            ConsoleWindow.getInstance().updateTerminalCount(-1);
            System.out.println("Terminal desconectado: " + exception.getMessage());
        }
    }
}