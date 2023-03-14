package org.gaidumax.services.interfaces;

import org.gaidumax.model.Client;

public interface ClientService {

    Client findByAddress(String address);

    Client save(Client client);

    void delete(Client client);
}
