package org.gaidumax.services.interfaces;

import org.gaidumax.model.Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface AuthenticationService {

    boolean authenticate(BufferedReader in, BufferedWriter out, Client client) throws IOException;
}
