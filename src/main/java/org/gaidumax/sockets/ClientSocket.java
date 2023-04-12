package org.gaidumax.sockets;

import org.gaidumax.model.Client;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import static org.gaidumax.sockets.Keys.CLIENT_KEYS;
import static org.gaidumax.sockets.Keys.SERVER_KEYS;
import static org.gaidumax.sockets.ServerCommands.*;

public class ClientSocket extends Thread {

    private final Client client;
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public ClientSocket(Client client, Socket socket) throws IOException {
        this.client = client;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                if (!client.isAuthenticated() && !authenticate()) {
                    break;
                }
            }
            closeAll();
            socket.close();
            System.out.println("Connection has ended with port=" + client.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean authenticate() throws IOException {
        // get username
        String usernameRequest = read();
        System.out.println("Username by port=" + client.getPort() + ":\t" + usernameRequest);
        if (!validate(usernameRequest, 20)) {
            send(SERVER_SYNTAX_ERROR);
            closeAll();
            return false;
        }
        client.setUsername(usernameRequest);
        send(SERVER_KEY_REQUEST);

        // get key, return server hash
        String keyRequest = read();
        System.out.println("Key by port=" + client.getPort() + ":\t" + keyRequest);
        if (!validate(keyRequest, 5)) {
            send(SERVER_SYNTAX_ERROR);
            closeAll();
            return false;
        }
        int key = Integer.parseInt(keyRequest);
        if (key < 0 || SERVER_KEYS.size() < key) {
            send(SERVER_KEY_OUT_OF_RANGE_ERROR);
            closeAll();
            return false;
        }
        int usernameHash = hash(client.getUsername());
        int serverHash = (usernameHash + SERVER_KEYS.get(key)) % 65536;
        System.out.println("Server hash to port=" + client.getPort() + ":\t" + serverHash);
        send(serverHash + "\7\10");

        // get user hash, confirm it
        String clientHashRequest = read();
        System.out.println("Client hash by port=" + client.getPort() + ":\t" + clientHashRequest);
        if (!validate(clientHashRequest, 7)) {
            send(SERVER_SYNTAX_ERROR);
            closeAll();
            return false;
        }
        int clientHash = Integer.parseInt(clientHashRequest);
        if (clientHash == (usernameHash + CLIENT_KEYS.get(key)) % 65536) {
            send(SERVER_OK);
            client.setAuthenticated(true);
            System.out.println("Client has been authenticated by port=" + client.getPort());
            return true;
        } else {
            send(SERVER_LOGIN_FAILED);
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

    private String read() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 2 || !sb.substring(sb.length() - 2).equals("\7\10")) {
            int input = in.read();
            if (input == -1) {
                return null;
            }
            sb.append((char) input);
        }
        return sb.substring(0, sb.length() - 2);
    }

    private void send(String response) throws IOException {
        out.write(response);
        out.flush();
    }

    private void closeAll() throws IOException {
        in.close();
        out.close();
    }
}
