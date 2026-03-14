package org.mege.config;

import org.mege.services.TerminalHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void start() throws IOException {
        ServerSocket server = new ServerSocket(6500);

        while (true) {
            Socket socket = server.accept();

            Thread t = new Thread(new TerminalHandler(socket));
            t.start();
        }
    }
}
