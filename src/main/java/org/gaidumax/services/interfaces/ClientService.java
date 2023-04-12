package org.gaidumax.services.interfaces;

import org.gaidumax.model.Client;

public interface ClientService {

    Client findByPort(int port);

    Client save(Client client);

    void delete(Client client);
}
