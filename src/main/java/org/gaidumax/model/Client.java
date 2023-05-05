package org.gaidumax.model;

import java.util.Objects;

public class Client {

    private final int port;
    private String username;
    private ClientAuthStatus authStatus = ClientAuthStatus.IS_SENDING_USERNAME;
    private int authKey = -1;

    public Client(int port) {
        this.port = port;
    }

    // getters

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAuthenticated() {
        return authStatus == ClientAuthStatus.AUTHENTICATED;
    }

    public ClientAuthStatus getAuthStatus() {
        return authStatus;
    }

    public int getAuthKey() {
        return authKey;
    }

    // setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthStatus(ClientAuthStatus authStatus) {
        this.authStatus = authStatus;
    }

    public void setAuthKey(int authKey) {
        this.authKey = authKey;
    }

    // data

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (port != client.port) return false;
        if (authKey != client.authKey) return false;
        if (!Objects.equals(username, client.username)) return false;
        return authStatus == client.authStatus;
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + authStatus.hashCode();
        result = 31 * result + authKey;
        return result;
    }

    @Override
    public String toString() {
        return "Client{" +
                "port=" + port +
                ", username='" + username + '\'' +
                ", authStatus=" + authStatus +
                ", authKey=" + authKey +
                '}';
    }
}
