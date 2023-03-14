package org.gaidumax.sockets;

import org.gaidumax.model.Client;
import org.gaidumax.services.interfaces.ClientService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server extends Thread {

    private final ServerSocket serverSocket;
    private final ClientService clientService;

    public Server(int port, ClientService clientService) {
        try {
            serverSocket = new ServerSocket(port);
            this.clientService = clientService;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Client client = clientService.findByAddress(socket.getInetAddress().toString());
                if (Objects.isNull(client)) {
                    client = new Client(socket.getInetAddress().toString(), socket.getPort());
                    clientService.save(client);
                    new ClientSocket(client, socket).start();
                }
                System.out.println(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
