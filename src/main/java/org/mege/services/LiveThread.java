package org.mege.services;

import java.io.OutputStream;
import java.net.Socket;

public class LiveThread {
    public Thread getThread(OutputStream outputStream, Socket socket) {
        Thread liveThread = new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    Thread.sleep(30000);
                    outputStream.write("#live?".getBytes());
                    outputStream.flush();
                    System.out.println("[LIVE] Sent #live? para " + socket.getInetAddress());
                }
            } catch (Exception e) {
                System.out.println("[LIVE] Heartbeat encerrado: " + e.getMessage());
            }
        });
        liveThread.setDaemon(true);
        return liveThread;
    }
}
