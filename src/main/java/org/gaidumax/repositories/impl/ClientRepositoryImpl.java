package org.gaidumax.repositories.impl;

import org.gaidumax.model.Client;
import org.gaidumax.repositories.interfaces.ClientRepository;

import java.util.ArrayList;
import java.util.List;

public class ClientRepositoryImpl implements ClientRepository {

    private final List<Client> clients = new ArrayList<>();

    @Override
    public Client findByPort(int port) {
        return clients.stream().filter(client -> client.getPort() == port)
                .findFirst().orElse(null);
    }

    @Override
    public Client save(Client client) {
        clients.add(client);
        return client;
    }

    @Override
    public void delete(Client client) {
        clients.remove(client);
    }
}
