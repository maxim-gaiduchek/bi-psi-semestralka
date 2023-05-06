package org.gaidumax.services.interfaces;

import org.gaidumax.model.Client;

import java.io.BufferedWriter;
import java.io.IOException;

public interface MovementService {

    void move(Client client, BufferedWriter out) throws IOException;

    boolean makeNextMove(Client client, BufferedWriter out, String request) throws IOException;
}
