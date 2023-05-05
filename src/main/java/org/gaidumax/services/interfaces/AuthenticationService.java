package org.gaidumax.services.interfaces;

import org.gaidumax.model.Client;

import java.io.BufferedWriter;
import java.io.IOException;

public interface AuthenticationService {

    boolean authenticate(Client client, BufferedWriter out, String request) throws IOException;
}
