package org.gaidumax.repositories.interfaces;

import org.gaidumax.model.Client;

public interface ClientRepository {

    Client findByPort(int port);

    Client save(Client client);

    void delete(Client client);
}
