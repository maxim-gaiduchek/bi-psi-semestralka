package org.gaidumax.sockets;

import org.gaidumax.model.Client;

import java.io.*;
import java.net.Socket;

public class ClientSocket extends Thread {

    private final Client client;
    private final Socket socket;

    public ClientSocket(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                String request = in.readLine();
                System.out.println(request);
                out.write(request);
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
