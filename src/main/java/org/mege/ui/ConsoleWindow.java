package org.mege.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleWindow {

    private static ConsoleWindow instance;
    private final JTextPane textPane;
    private final JLabel statusLabel;
    private int terminalCount = 0;

    private ConsoleWindow() {
        JFrame frame = new JFrame("MEGE - Monitor de Terminais VP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLayout(new BorderLayout());

        // Painel de Texto (Console)
        textPane = new JTextPane();
        textPane.setBackground(new Color(30, 30, 30)); // Fundo grafite escuro
        textPane.setEditable(false);
        textPane.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scroll, BorderLayout.CENTER);

        // Barra de Status Inferior
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 30));
        statusPanel.setBackground(new Color(45, 45, 45));

        statusLabel = new JLabel(" Terminais Ativos: 0 | Servidor rodando na porta 6500");
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton clearBtn = new JButton("Limpar Console");
        clearBtn.setFocusable(false);
        clearBtn.addActionListener(e -> textPane.setText(""));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(clearBtn, BorderLayout.EAST);
        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        redirectSystemOut();
    }

    public static ConsoleWindow getInstance() {
        if (instance == null) {
            instance = new ConsoleWindow();
        }
        return instance;
    }

    // Método para atualizar o contador de terminais na UI
    public void updateTerminalCount(int delta) {
        this.terminalCount += delta;
        SwingUtilities.invokeLater(() ->
                statusLabel.setText(" Terminais Ativos: " + terminalCount + " | Servidor rodando na porta 6500")
        );
    }

    private void redirectSystemOut() {
        OutputStream out = new OutputStream() {
            private StringBuilder lineBuffer = new StringBuilder();

            @Override
            public void write(int b) {
                char c = (char) b;
                lineBuffer.append(c);
                if (c == '\n') {
                    processLine(lineBuffer.toString());
                    lineBuffer.setLength(0);
                }
            }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void processLine(String line) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Color color = Color.WHITE; // Cor padrão

        // Lógica de cores baseada no conteúdo
        if (line.contains("[LIVE]")) color = new Color(100, 149, 237); // Azul claro
        else if (line.contains("Connected")) color = new Color(50, 205, 50); // Verde Lima
        else if (line.contains("desconectado") || line.contains("error") || line.contains("Exception")) color = new Color(255, 69, 0); // Laranja/Vermelho
        else if (line.contains("Recebido")) color = new Color(218, 165, 32); // Dourado
        else if (line.contains("Enviei #ok")) color = Color.CYAN;

        appendColoredText("[" + timestamp + "] ", new Color(120, 120, 120)); // Timestamp cinza
        appendColoredText(line, color);
    }

    private void appendColoredText(String text, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                Style style = textPane.addStyle("ColorStyle", null);
                StyleConstants.setForeground(style, color);
                doc.insertString(doc.getLength(), text, style);
                textPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }
}