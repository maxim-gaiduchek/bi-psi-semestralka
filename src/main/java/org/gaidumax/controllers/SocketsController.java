package org.gaidumax.controllers;

import org.gaidumax.services.interfaces.ClientService;
import org.gaidumax.sockets.Server;

import java.util.ArrayList;
import java.util.List;

public class SocketsController {

    private final ClientService clientService;
    private final List<Server> serverPool = new ArrayList<>();

    public SocketsController(ClientService clientService) {
        this.clientService = clientService;
    }

    public void startServer(int port) {
        Server server = new Server(port, clientService);
        server.start();
        serverPool.add(server);
    }
}
