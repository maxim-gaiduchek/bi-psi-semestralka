package org.gaidumax.services.impl;

import org.gaidumax.model.Client;
import org.gaidumax.repositories.interfaces.ClientRepository;
import org.gaidumax.services.interfaces.ClientService;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client findByPort(int port) {
        return clientRepository.findByPort(port);
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public void delete(Client client) {
        clientRepository.delete(client);
    }
}
