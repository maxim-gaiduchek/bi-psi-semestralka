package org.gaidumax.model;

public class Client {

    private final int port;
    private String username;
    private boolean authenticated = false;

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
        return authenticated;
    }

    // setters

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    // data

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        if (port != client.port) return false;
        return authenticated == client.authenticated;
    }

    @Override
    public int hashCode() {
        return port;
    }

    @Override
    public String toString() {
        return "Client{" +
                "port=" + port +
                ", authenticated=" + authenticated +
                '}';
    }
}
