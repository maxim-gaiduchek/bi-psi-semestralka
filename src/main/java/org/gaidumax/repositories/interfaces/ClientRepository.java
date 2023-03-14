package org.gaidumax.repositories.interfaces;

import org.gaidumax.model.Client;

public interface ClientRepository {

    Client findByAddress(String address);

    Client save(Client client);

    void delete(Client client);
}
