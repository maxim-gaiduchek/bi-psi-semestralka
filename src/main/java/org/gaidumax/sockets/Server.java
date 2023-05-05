package org.gaidumax.sockets;

import org.gaidumax.model.Client;
import org.gaidumax.services.interfaces.ClientStorageService;
import utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server extends Thread {

    private final Logger logger = new Logger("Server");

    private final ServerSocket serverSocket;
    private final ClientStorageService clientStorageService;

    public Server(int port, ClientStorageService clientStorageService) {
        try {
            serverSocket = new ServerSocket(port);
            this.clientStorageService = clientStorageService;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Client client = clientStorageService.findByPort(socket.getLocalPort());
                if (Objects.isNull(client)) {
                    client = new Client(socket.getPort());
                    clientStorageService.save(client);
                    logger.log("New client has been registered:\t" + client);
                } else {
                    logger.log("Old client has been connected:\t" + client);
                }
                new ClientSocket(client, socket).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
