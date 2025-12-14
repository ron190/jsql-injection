package com.jsql.model.injection.strategy;

import com.jsql.model.InjectionModel;
import com.jsql.util.LogLevelUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xbill.DNS.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class DnsServer {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public InjectionModel injectionModel;
    public DatagramSocket socket;
    public List<String> results = new ArrayList<>();

    public DnsServer(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
        try {
            this.socket = new DatagramSocket(null);
        } catch (SocketException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e);
        }
    }

    public void listen() {
        this.socket.close();  // unbind if already connected
        int port = Integer.parseInt(this.injectionModel.getMediatorUtils().getPreferencesUtil().getDnsPort());
        var domainName = this.injectionModel.getMediatorUtils().getPreferencesUtil().getDnsDomain();
        this.results.clear();
        try (var newSocket = new DatagramSocket(port)) {
            this.socket = newSocket;
            LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "DNS listening on port [{}] for [{}]...", port, domainName);

            byte[] buffer = new byte[512];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    this.socket.receive(packet);
                } catch (SocketException e) {  // expected when socket is closed
                    LOGGER.log(LogLevelUtil.IGNORE, e, e);
                    break;
                }

                Message query = new Message(packet.getData());
                Record question = query.getQuestion();
                Name name = question.getName();

                if (name.toString().contains(domainName)) {
                    this.results.add(name.toString());
                }

                // Build response
                Message response = new Message(query.getHeader().getID());
                response.getHeader().setFlag(Flags.QR); // Response
                response.addRecord(question, Section.QUESTION);

                response.addRecord(
                    Record.fromString(
                        Name.fromString(domainName +"."),
                        Type.A,
                        DClass.IN,
                        86400,
                        "127.0.0.1",
                        Name.fromString(domainName +".")
                    ),
                    Section.ANSWER
                );

                byte[] responseData = response.toWire();
                DatagramPacket responsePacket = new DatagramPacket(
                    responseData,
                    responseData.length,
                    packet.getAddress(),
                    packet.getPort()
                );
                this.socket.send(responsePacket);
            }
        } catch (IOException e) {
            LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e);
        }
    }

    public void close() {
        this.socket.close();
    }

    public List<String> getResults() {
        return this.results;
    }
}