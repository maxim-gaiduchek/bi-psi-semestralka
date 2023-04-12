package org.gaidumax;

import org.gaidumax.controllers.SocketsController;
import org.gaidumax.repositories.impl.ClientRepositoryImpl;
import org.gaidumax.repositories.interfaces.ClientRepository;
import org.gaidumax.services.impl.ClientServiceImpl;
import org.gaidumax.services.interfaces.ClientService;

import java.io.IOException;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        ClientRepository clientRepository = new ClientRepositoryImpl();
        ClientService clientService = new ClientServiceImpl(clientRepository);
        SocketsController socketsController = new SocketsController(clientService);
        socketsController.startServer(8888);
        try (Socket socket = new Socket("localhost", 8888)) {
            System.out.println("Server has been started:\t" + socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}