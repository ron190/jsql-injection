package com.jsql.view.swing.terminal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerInput {

    private ServerSocket echoServer = null;
    private final int port;
    private ServerInputConnection oneconnection;
    private final ExploitReverseShell exploitReverseShell;

    public ServerInput(ExploitReverseShell exploitReverseShell, int port) {
        this.port = port;
        this.exploitReverseShell = exploitReverseShell;
    }

    public void startServer() throws IOException, InterruptedException {
        this.echoServer = new ServerSocket(this.port);  // port less than 1024 if root
        this.echoServer.setSoTimeout(10000);
        Socket clientSocket = this.echoServer.accept();
        this.oneconnection = new ServerInputConnection(this.exploitReverseShell, clientSocket, this);
        this.oneconnection.run();
    }

    void close() throws IOException {
        this.echoServer.close();
    }

    public ServerInputConnection getOneconnection() {
        return this.oneconnection;
    }
}
