package org.gaidumax.model;

public enum ClientAuthStatus {

    IS_SENDING_USERNAME(20),
    IS_SENDING_KEY(5),
    IS_SENDING_HASH(7),
    AUTHENTICATED(12);

    private final int expectedMaxLength;

    ClientAuthStatus(int expectedMaxLength) {
        this.expectedMaxLength = expectedMaxLength;
    }

    public int getExpectedMaxLength() {
        return expectedMaxLength;
    }
}
