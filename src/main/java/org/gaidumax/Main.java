package org.gaidumax;

import org.gaidumax.controllers.SocketsController;
import org.gaidumax.repositories.impl.ClientRepositoryImpl;
import org.gaidumax.repositories.interfaces.ClientRepository;
import org.gaidumax.services.impl.ClientStorageServiceImpl;
import org.gaidumax.services.interfaces.ClientStorageService;

import java.io.IOException;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        ClientRepository clientRepository = new ClientRepositoryImpl();
        ClientStorageService clientStorageService = new ClientStorageServiceImpl(clientRepository);
        SocketsController socketsController = new SocketsController(clientStorageService);
        socketsController.startServer(8888);
        try (Socket socket = new Socket("localhost", 8888)) {
            System.out.println("Server has been started:\t" + socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}