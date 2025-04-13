package com.jsql.view.swing.terminal;

import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerInputConnection {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final BufferedReader bufferedReader;
    private final Socket clientSocket;
    private final ServerInput serverInput;
    private final ExploitReverseShell exploitReverseShell;
    private boolean running = true;
    private String command;

    public ServerInputConnection(ExploitReverseShell exploitReverseShell, Socket clientSocket, ServerInput serverInput) throws IOException {
        this.clientSocket = clientSocket;
        this.exploitReverseShell = exploitReverseShell;
        this.serverInput = serverInput;
        LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Reverse established by {}", clientSocket);
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Type 'exit' in reverse shell to close the connection");
        this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() throws IOException {
        try (DataOutputStream dataOutputStream = new DataOutputStream(this.clientSocket.getOutputStream())) {
            Thread readerThread = new Thread(() -> {
                try {
                    this.handleSocketReading();
                } catch (IOException e) {
                    LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Error reading from socket: {}", e.getMessage());
                } finally {
                    this.closeResources();
                }
            });
            readerThread.start();

            while (this.running) {
                this.processAndSendCommand(dataOutputStream);
            }

            try {
                readerThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "Reader thread interrupted");
            }
        }
    }

    private void processAndSendCommand(DataOutputStream dataOutputStream) throws IOException {
        if (StringUtils.isNotEmpty(this.command)) {
            var commandWithoutPrompt = this.command.replaceAll("[^$]*\\$\\s*", "");
            this.command = null;
            dataOutputStream.writeBytes(commandWithoutPrompt + "\n");
        }
    }

    private void handleSocketReading() throws IOException {
        int length = 1024;
        char[] buffer = new char[length];
        int charsRead;
        while (this.running) {
            charsRead = this.bufferedReader.read(buffer, 0, length);
            if (charsRead != -1) {
                String result = new String(buffer, 0, charsRead);  // discard unused chars from buffer
                this.exploitReverseShell.append(result.matches("\\$$") ? result +" " : result);  // space after internal prompt
                this.exploitReverseShell.reset(false);
            } else {
                break;
            }
        }
    }

    private void closeResources() {
        try {
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Reverse connection closed");
            this.running = false;
            this.serverInput.close();
        } catch (IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Error closing resources: " + e.getMessage());
        }
    }

    public void setCommand(String command) {
        this.command = command;
    }
}