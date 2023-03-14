package org.gaidumax.services.impl;

import org.gaidumax.model.Client;
import org.gaidumax.repositories.interfaces.ClientRepository;
import org.gaidumax.services.interfaces.ClientService;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;

    public ClientServiceImpl(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public Client findByAddress(String address) {
        return repository.findByAddress(address);
    }

    @Override
    public Client save(Client client) {
        return repository.save(client);
    }

    @Override
    public void delete(Client client) {
        repository.delete(client);
    }
}
