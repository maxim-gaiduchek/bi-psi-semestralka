package org.gaidumax.services.interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface RechargingService {

    boolean isRechargingCommand(String request);

    boolean recharge(BufferedReader in, BufferedWriter out) throws IOException;
}
