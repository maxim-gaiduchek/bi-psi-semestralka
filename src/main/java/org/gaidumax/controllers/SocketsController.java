package org.gaidumax.controllers;

import org.gaidumax.services.interfaces.ClientStorageService;
import org.gaidumax.sockets.Server;

import java.util.ArrayList;
import java.util.List;

public class SocketsController {

    private final ClientStorageService clientStorageService;
    private final List<Server> serverPool = new ArrayList<>();

    public SocketsController(ClientStorageService clientStorageService) {
        this.clientStorageService = clientStorageService;
    }

    public void startServer(int port) {
        Server server = new Server(port, clientStorageService);
        server.start();
        serverPool.add(server);
    }
}
