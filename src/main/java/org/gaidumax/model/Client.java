package org.gaidumax.model;

import java.util.Objects;

public class Client {
    private String address;
    private int port;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    // getters

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    // setters

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // data

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return port == client.port && address.equals(client.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

    @Override
    public String toString() {
        return "Client{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
