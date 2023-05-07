package org.gaidumax.services.impl;

import org.gaidumax.services.interfaces.IOService;
import org.gaidumax.services.interfaces.RechargingService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

import static org.gaidumax.sockets.ClientCommands.CLIENT_FULL_POWER;
import static org.gaidumax.sockets.ClientCommands.CLIENT_RECHARGING;
import static org.gaidumax.sockets.ServerCommands.SERVER_LOGIC_ERROR;

public class RechargingServiceImpl implements RechargingService {

    private static final int TIMEOUT_RECHARGING = 5; // in seconds;

    private final IOService ioService = new IOServiceImpl();

    @Override
    public boolean isRechargingCommand(String request) {
        return CLIENT_RECHARGING.equals(request);
    }

    @Override
    public boolean recharge(BufferedReader in, BufferedWriter out, Socket socket, int defaultTimeout)
            throws IOException {
        socket.setSoTimeout(TIMEOUT_RECHARGING * 1000);
        long start = System.currentTimeMillis();
        String request = ioService.read(in, 12);
        long finish = System.currentTimeMillis();
        if (!CLIENT_FULL_POWER.equals(request)) {
            ioService.send(out, SERVER_LOGIC_ERROR);
            return false;
        }
        socket.setSoTimeout(defaultTimeout * 1000);
        return finish - start <= TIMEOUT_RECHARGING * 1000;
    }
}
