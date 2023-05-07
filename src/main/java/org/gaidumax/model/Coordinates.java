package org.gaidumax.model;

public record Coordinates(int x, int y) {

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    public boolean isVertical() {
        return y != 0;
    }

    public boolean isHorizontal() {
        return x != 0;
    }
}
