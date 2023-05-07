package org.gaidumax.services.impl;

import org.gaidumax.model.Client;
import org.gaidumax.model.ClientMove;
import org.gaidumax.model.Coordinates;
import org.gaidumax.services.interfaces.IOService;
import org.gaidumax.services.interfaces.MovementService;
import org.gaidumax.utils.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.gaidumax.sockets.ServerCommands.SERVER_MOVE;
import static org.gaidumax.sockets.ServerCommands.SERVER_SYNTAX_ERROR;
import static org.gaidumax.sockets.ServerCommands.SERVER_TURN_LEFT;
import static org.gaidumax.sockets.ServerCommands.SERVER_TURN_RIGHT;

public class MovementServiceImpl implements MovementService {

    private final Logger logger = new Logger("MovementService");
    private final IOService ioService = new IOServiceImpl();

    @Override
    public void move(Client client, BufferedWriter out) throws IOException {
        moveTo(client, out, SERVER_MOVE, ClientMove.MOVE);
    }

    private void logByLastMove(Client client) {
        logger.log("Client with port=" + client.getPort() + " moved:\t" + client.getLastMove());
    }

    @Override
    public boolean makeNextMove(Client client, BufferedWriter out, String request) throws IOException {
        if (request == null) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            logger.log("Client with port=" + client.getPort() + " has sent invalid request");
            return false;
        }
        Coordinates currXY = parseCoordinates(request);
        if (currXY == null) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            logger.log("Client with port=" + client.getPort() + " has sent invalid coords:\t" + request);
            return false;
        }
        if (currXY.isZero()) {
            logger.log("Client with port=" + client.getPort() + " has reached a goal!!!");
            return true;
        }
        Coordinates clientXY = client.getCoordinates();
        client.setCoordinates(currXY);
        if (clientXY == null) {
            ioService.send(out, SERVER_MOVE);
            client.setLastMove(ClientMove.MOVE);
            logByLastMove(client);
            return false;
        }
        Coordinates rotationToZero = calcVectorToZero(currXY), currRotation;
        // if stopped (obstacle)
        if (currXY.equals(clientXY)) {
            Coordinates clientRotation = client.getRotation();
            // move if it is first moves
            if (clientRotation == null) {
                switch (client.getLastMove()) {
                    case MOVE -> turnRight(client, out);
                    case TURN_LEFT, TURN_RIGHT -> move(client, out);
                }
                return false;
            }
            // move forward
            if (client.getLastMove() != ClientMove.MOVE && haveSimilarDirection(clientRotation, rotationToZero)) {
                move(client, out);
                return false;
            }
            currRotation = clientRotation;
            // turn to horizontal
            if (turnToHorizontal(client, out, currRotation, rotationToZero)) {
                return false;
            }
            // turn to vertical
            if (turnToVertical(client, out, currRotation, rotationToZero)) {
                return false;
            }
            turnRight(client, out);
            client.setRotation(null);
            return false;
        }
        currRotation = calcVector(clientXY, currXY);
        // move forward
        if (haveSimilarDirection(currRotation, rotationToZero)) {
            move(client, out);
            client.setRotation(currRotation);
            return false;
        }
        // turn to horizontal
        if (turnToHorizontal(client, out, currRotation, rotationToZero)) {
            return false;
        }
        // turn to vertical
        if (turnToVertical(client, out, currRotation, rotationToZero)) {
            return false;
        }
        // turn 180 deg
        if (currRotation.isHorizontal()) {
            currRotation = new Coordinates(0, currRotation.x() * (-1));
        } else if (currRotation.isVertical()) {
            currRotation = new Coordinates(currRotation.y(), 0);
        }
        turnRight(client, out);
        client.setRotation(currRotation);
        return false;
    }

    private void turnRight(Client client, BufferedWriter out) throws IOException {
        moveTo(client, out, SERVER_TURN_RIGHT, ClientMove.TURN_RIGHT);
    }

    private void turnLeft(Client client, BufferedWriter out) throws IOException {
        moveTo(client, out, SERVER_TURN_LEFT, ClientMove.TURN_LEFT);
    }

    private void moveTo(Client client, BufferedWriter out, String message, ClientMove move) throws IOException {
        ioService.send(out, message);
        client.setLastMove(move);
        logByLastMove(client);
    }

    private boolean turnToHorizontal(Client client, BufferedWriter out,
                                     Coordinates currRotation, Coordinates rotationToZero) throws IOException {
        if (!currRotation.isVertical() || !rotationToZero.isHorizontal()) {
            return false;
        }
        int direction = currRotation.y() * rotationToZero.x();
        if (direction > 0) {
            turnRight(client, out);
        } else if (direction < 0) {
            turnLeft(client, out);
        }
        if (currRotation.y() < 0) {
            direction *= -1;
        }
        currRotation = new Coordinates(direction, 0);
        client.setRotation(currRotation);
        return true;
    }

    private boolean turnToVertical(Client client, BufferedWriter out,
                                   Coordinates currRotation, Coordinates rotationToZero) throws IOException {
        if (!currRotation.isHorizontal() || !rotationToZero.isVertical()) {
            return false;
        }
        int direction = currRotation.x() * rotationToZero.y();
        if (direction < 0) {
            turnRight(client, out);
        } else if (direction > 0) {
            turnLeft(client, out);
        }
        if (currRotation.x() < 0) {
            direction *= -1;
        }
        currRotation = new Coordinates(0, direction);
        client.setRotation(currRotation);
        return true;
    }

    private Coordinates parseCoordinates(String request) {
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
        return new Coordinates(x, y);
    }

    private Coordinates calcVectorToZero(Coordinates from) {
        return calcVector(from, new Coordinates(0, 0));
    }

    private Coordinates calcVector(Coordinates from, Coordinates to) {
        return new Coordinates(
                Integer.compare(to.x(), from.x()),
                Integer.compare(to.y(), from.y())
        );
    }

    private boolean haveSimilarDirection(Coordinates first, Coordinates second) {
        return first.isHorizontal() && second.isHorizontal() && first.x() == second.x() ||
                first.isVertical() && second.isVertical() && first.y() == second.y();
    }
}
