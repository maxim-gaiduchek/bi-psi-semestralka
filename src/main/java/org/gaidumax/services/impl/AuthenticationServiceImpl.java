package org.gaidumax.services.impl;

import org.gaidumax.model.Client;
import org.gaidumax.model.ClientAuthStatus;
import org.gaidumax.services.interfaces.AuthenticationService;
import org.gaidumax.services.interfaces.IOService;
import org.gaidumax.services.interfaces.MovementService;
import org.gaidumax.utils.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

import static org.gaidumax.sockets.Keys.CLIENT_KEYS;
import static org.gaidumax.sockets.Keys.SERVER_KEYS;
import static org.gaidumax.sockets.ServerCommands.SERVER_KEY_OUT_OF_RANGE_ERROR;
import static org.gaidumax.sockets.ServerCommands.SERVER_KEY_REQUEST;
import static org.gaidumax.sockets.ServerCommands.SERVER_LOGIC_ERROR;
import static org.gaidumax.sockets.ServerCommands.SERVER_LOGIN_FAILED;
import static org.gaidumax.sockets.ServerCommands.SERVER_OK;
import static org.gaidumax.sockets.ServerCommands.SERVER_SYNTAX_ERROR;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final Logger logger = new Logger("AuthService");

    private final IOService ioService = new IOServiceImpl();
    private final MovementService movementService = new MovementServiceImpl();

    @Override
    public boolean authenticate(Client client, BufferedWriter out, String request) throws IOException {
        switch (client.getAuthStatus()) {
            case IS_SENDING_USERNAME -> {
                if (!parseUsername(client, out, request)) {
                    return false;
                }
            }
            case IS_SENDING_KEY -> {
                if (!parseKey(client, out, request)) {
                    return false;
                }
            }
            case IS_SENDING_HASH -> {
                if (!parseHash(client, out, request)) {
                    return false;
                }
            }
            default -> {
                ioService.send(out, SERVER_LOGIC_ERROR);
                return false;
            }
        }
        return true;
    }

    private boolean parseUsername(Client client, BufferedWriter out, String request) throws IOException {
        logger.log("Username by port=" + client.getPort() + ":\t" + request);
        if (!validate(request, 20)) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            logger.log("Username by port=" + client.getPort() + " is invalid");
            return false;
        }
        client.setUsername(request);
        client.setAuthStatus(ClientAuthStatus.IS_SENDING_KEY);
        ioService.send(out, SERVER_KEY_REQUEST);
        return true;
    }

    private boolean parseKey(Client client, BufferedWriter out, String request) throws IOException {
        logger.log("Key by port=" + client.getPort() + ":\t" + request);
        if (!validate(request, 5)) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            logger.log("Key by port=" + client.getPort() + " is invalid");
            return false;
        }
        int key = Integer.parseInt(request);
        if (key < 0 || SERVER_KEYS.size() <= key) {
            ioService.send(out, SERVER_KEY_OUT_OF_RANGE_ERROR);
            logger.log("Key by port=" + client.getPort() + " is out of range");
            return false;
        }
        int usernameHash = hash(client.getUsername());
        int serverHash = (usernameHash + SERVER_KEYS.get(key)) % 65536;
        logger.log("Server hash to port=" + client.getPort() + ":\t" + serverHash);
        client.setAuthKey(key);
        client.setAuthStatus(ClientAuthStatus.IS_SENDING_HASH);
        ioService.send(out, serverHash + "\7\10");
        return true;
    }

    private boolean parseHash(Client client, BufferedWriter out, String request) throws IOException {
        logger.log("Client's hash by port=" + client.getPort() + ":\t" + request);
        if (!validate(request, 7)) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            logger.log("Client's hash by port=" + client.getPort() + " is invalid");
            return false;
        }
        int usernameHash = hash(client.getUsername());
        int clientHash = Integer.parseInt(request);
        if (clientHash != (usernameHash + CLIENT_KEYS.get(client.getAuthKey())) % 65536) {
            ioService.send(out, SERVER_LOGIN_FAILED);
            logger.log("Client has not been authenticated by port=" + client.getPort());
            return false;
        }
        ioService.send(out, SERVER_OK);
        logger.log("Client has been authenticated by port=" + client.getPort());
        client.setAuthStatus(ClientAuthStatus.AUTHENTICATED);
        movementService.move(client, out);
        return true;
    }

    private boolean validate(String request, int maxLength) {
        return Objects.nonNull(request) && request.length() <= maxLength - 2; // \a\b == "\7\10"
    }

    private int hash(String str) {
        int res = 0;
        for (char ch : str.toCharArray()) {
            res = (res + ch * 1000) % 65536;
        }
        return res;
    }
}
