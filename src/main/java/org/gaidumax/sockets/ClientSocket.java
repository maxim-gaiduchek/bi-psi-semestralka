package org.gaidumax.sockets;

import org.gaidumax.model.Client;
import org.gaidumax.services.impl.AuthenticationServiceImpl;
import org.gaidumax.services.impl.IOServiceImpl;
import org.gaidumax.services.impl.RechargingServiceImpl;
import org.gaidumax.services.interfaces.AuthenticationService;
import org.gaidumax.services.interfaces.IOService;
import org.gaidumax.services.interfaces.RechargingService;
import utils.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientSocket extends Thread {

    private final Logger logger = new Logger("Client");

    private static final int TIMEOUT = 1; // in seconds

    private final Client client;
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    private final IOService ioService = new IOServiceImpl();
    private final AuthenticationService authService = new AuthenticationServiceImpl();
    private final RechargingService rechargingService = new RechargingServiceImpl();

    public ClientSocket(Client client, Socket socket) throws IOException {
        this.client = client;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        socket.setSoTimeout(TIMEOUT * 1000);
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                String request = ioService.read(in);
                if (rechargingService.isRechargingCommand(request)) {
                    if (rechargingService.recharge(in, out)) {
                        continue;
                    } else {
                        break;
                    }
                }
                if (!client.isAuthenticated()) {
                    if (authService.authenticate(client, out, request)) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
            logger.log("Connection has ended with port=" + client.getPort());
            closeAll();
        } catch (SocketTimeoutException e) {
            logger.log("Connection time is out on port=" + client.getPort());
            closeAll();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void closeAll() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
