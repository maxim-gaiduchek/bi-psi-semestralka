package org.gaidumax.services.interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface IOService {

    String read(BufferedReader in) throws IOException;

    void send(BufferedWriter out, String message) throws IOException;
}
