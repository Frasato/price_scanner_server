package org.mege;

import org.mege.config.Server;
import org.mege.ui.ConsoleWindow;
import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(ConsoleWindow::getInstance);

        Server server = new Server();
        server.start();
    }
}