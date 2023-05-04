package org.gaidumax.sockets;

import org.gaidumax.model.Client;
import org.gaidumax.services.impl.AuthenticationServiceImpl;
import org.gaidumax.services.impl.IOServiceImpl;
import org.gaidumax.services.interfaces.AuthenticationService;
import org.gaidumax.services.interfaces.IOService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientSocket extends Thread {

    private final Client client;
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    private final IOService ioService = new IOServiceImpl();
    private final AuthenticationService authService = new AuthenticationServiceImpl();

    public ClientSocket(Client client, Socket socket) throws IOException {
        this.client = client;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                //String request = ioService.read(in);
                if (!client.isAuthenticated() && !authService.authenticate(in, out, client)) {
                    break;
                }
            }
            closeAll();
            System.out.println("Connection has ended with port=" + client.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeAll() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
