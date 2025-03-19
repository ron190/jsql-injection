package com.jsql.view.swing.terminal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerInput {

    private ServerSocket serverSocket = null;
    private final int port;
    private ServerInputConnection serverInputConnection;
    private final ExploitReverseShell exploitReverseShell;

    public ServerInput(ExploitReverseShell exploitReverseShell, int port) {
        this.port = port;
        this.exploitReverseShell = exploitReverseShell;
    }

    public void startServer() throws IOException, InterruptedException {
        this.serverSocket = new ServerSocket(this.port);  // port less than 1024 if root
        this.serverSocket.setSoTimeout(10000);
        Socket clientSocket = this.serverSocket.accept();
        this.serverInputConnection = new ServerInputConnection(this.exploitReverseShell, clientSocket, this);
        this.serverInputConnection.run();
    }

    void close() throws IOException {
        this.serverSocket.close();
    }

    public ServerInputConnection getServerInputConnection() {
        return this.serverInputConnection;
    }
}
