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
                Client client = clientService.findByPort(socket.getLocalPort());
                if (Objects.isNull(client)) {
                    client = new Client(socket.getPort());
                    clientService.save(client);
                    System.out.println("New client has been registered:\t" + client);
                } else {
                    System.out.println("Old client has been connected:\t" + client);
                }
                new ClientSocket(client, socket).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
