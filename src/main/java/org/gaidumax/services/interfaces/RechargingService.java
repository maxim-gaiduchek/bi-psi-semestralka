package org.gaidumax.services.interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public interface RechargingService {

    boolean isRechargingCommand(String request);

    boolean recharge(BufferedReader in, BufferedWriter out, Socket socket, int defaultTimeout) throws IOException;
}
