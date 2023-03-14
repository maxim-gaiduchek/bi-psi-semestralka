package org.gaidumax;

import org.gaidumax.controllers.SocketsController;
import org.gaidumax.repositories.impl.ClientRepositoryImpl;
import org.gaidumax.repositories.interfaces.ClientRepository;
import org.gaidumax.services.impl.ClientServiceImpl;
import org.gaidumax.services.interfaces.ClientService;

public class Main {

    public static void main(String[] args) {
        ClientRepository clientRepository = new ClientRepositoryImpl();
        ClientService clientService = new ClientServiceImpl(clientRepository);
        SocketsController socketsController = new SocketsController(clientService);
        socketsController.startServer(8888);
    }
}