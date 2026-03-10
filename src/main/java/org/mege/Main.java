package org.mege;

import org.mege.config.Server;
import java.io.IOException;

public class Main {
    static void main() throws IOException {
        Server server = new Server();
        server.start();
    }
}