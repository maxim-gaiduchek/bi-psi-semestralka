package org.gaidumax.services.impl;

import org.gaidumax.model.Client;
import org.gaidumax.services.interfaces.AuthenticationService;
import org.gaidumax.services.interfaces.IOService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

import static org.gaidumax.sockets.Keys.CLIENT_KEYS;
import static org.gaidumax.sockets.Keys.SERVER_KEYS;
import static org.gaidumax.sockets.ServerCommands.SERVER_KEY_OUT_OF_RANGE_ERROR;
import static org.gaidumax.sockets.ServerCommands.SERVER_KEY_REQUEST;
import static org.gaidumax.sockets.ServerCommands.SERVER_LOGIN_FAILED;
import static org.gaidumax.sockets.ServerCommands.SERVER_OK;
import static org.gaidumax.sockets.ServerCommands.SERVER_SYNTAX_ERROR;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final IOService ioService = new IOServiceImpl();

    @Override
    public boolean authenticate(BufferedReader in, BufferedWriter out, Client client) throws IOException {
        // get username
        String usernameRequest = ioService.read(in);
        System.out.println("Username by port=" + client.getPort() + ":\t" + usernameRequest);
        if (!validate(usernameRequest, 20)) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            return false;
        }
        client.setUsername(usernameRequest);
        ioService.send(out, SERVER_KEY_REQUEST);

        // get key, return server hash
        String keyRequest = ioService.read(in);
        System.out.println("Key by port=" + client.getPort() + ":\t" + keyRequest);
        if (!validate(keyRequest, 5)) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            return false;
        }
        int key = Integer.parseInt(keyRequest);
        if (key < 0 || SERVER_KEYS.size() < key) {
            ioService.send(out, SERVER_KEY_OUT_OF_RANGE_ERROR);
            return false;
        }
        int usernameHash = hash(client.getUsername());
        int serverHash = (usernameHash + SERVER_KEYS.get(key)) % 65536;
        System.out.println("Server hash to port=" + client.getPort() + ":\t" + serverHash);
        ioService.send(out, serverHash + "\7\10");

        // get user hash, confirm it
        String clientHashRequest = ioService.read(in);
        System.out.println("Client hash by port=" + client.getPort() + ":\t" + clientHashRequest);
        if (!validate(clientHashRequest, 7)) {
            ioService.send(out, SERVER_SYNTAX_ERROR);
            return false;
        }
        int clientHash = Integer.parseInt(clientHashRequest);
        if (clientHash == (usernameHash + CLIENT_KEYS.get(key)) % 65536) {
            ioService.send(out, SERVER_OK);
            client.setAuthenticated(true);
            System.out.println("Client has been authenticated by port=" + client.getPort());
            return true;
        } else {
            ioService.send(out, SERVER_LOGIN_FAILED);
            System.out.println("Client has not been authenticated by port=" + client.getPort());
            return false;
        }
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
