package org.gaidumax.services.impl;

import org.gaidumax.model.Client;
import org.gaidumax.services.interfaces.IOService;
import org.gaidumax.services.interfaces.MovementService;
import org.gaidumax.utils.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.gaidumax.sockets.ServerCommands.SERVER_MOVE;
import static org.gaidumax.sockets.ServerCommands.SERVER_SYNTAX_ERROR;

public class MovementServiceImpl implements MovementService {

    private final Logger logger = new Logger("MovementService");
    private final IOService ioService = new IOServiceImpl();

    @Override
    public void move(Client client, BufferedWriter out) throws IOException {
        ioService.send(out, SERVER_MOVE);
        logger.log("Client with port=" + client.getPort() + " moved:\tMOVE");
    }

    @Override
    public boolean makeNextMove(Client client, BufferedWriter out, String request) throws IOException {
        Pair pair = parseCoordinates(request);
        if (pair == null) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            logger.log("Client with port=" + client.getPort() + " has sent invalid coords:\t" + request);
            return false;
        }
        if (pair.isGoal()) {
            logger.log("Client with port=" + client.getPort() + " has reached a goal!!!");
            return true;
        }
        return false;
    }

    private Pair parseCoordinates(String request) {
        Pattern pattern = Pattern.compile("OK (-?\\d+) (-?\\d+)");
        Matcher matcher = pattern.matcher(request);
        if (!matcher.matches()) {
            return null;
        }
        int x, y;
        try {
            x = Integer.parseInt(matcher.group(1));
            y = Integer.parseInt(matcher.group(2));
        } catch (NumberFormatException e) {
            return null;
        }
        return new Pair(x, y);
    }

    private record Pair(int x, int y) {

        boolean isGoal() {
            return x == 0 && y == 0;
        }
    }
}
