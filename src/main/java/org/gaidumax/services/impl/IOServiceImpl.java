package org.gaidumax.services.impl;

import org.gaidumax.services.interfaces.IOService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import static org.gaidumax.sockets.ServerCommands.END;

public class IOServiceImpl implements IOService {

    @Override
    public String read(BufferedReader in) throws IOException {
        return read(in, -1);
    }

    @Override
    public String read(BufferedReader in, int expectedMaxLength) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (!isEnding(sb)) {
            int input = in.read();
            if (input == -1) {
                return null;
            }
            sb.append((char) input);
            if (expectedMaxLength >= 0 && expectedLength(sb) > expectedMaxLength) {
                return null;
            }
        }
        return sb.substring(0, sb.length() - 2);
    }

    private boolean isEnding(StringBuilder sb) {
        return getLast(sb, END.length()).equals(END);
    }

    private String getLast(StringBuilder sb, int size) {
        size = Math.min(size, sb.length());
        return sb.substring(sb.length() - size);
    }

    private int expectedLength(StringBuilder sb) {
        for (int length = END.length(); length > 0; length--) {
            if (getLast(sb, length).equals(END.substring(0, length))) {
                return sb.length() + END.length() - length;
            }
        }
        return sb.length() + END.length();
    }

    @Override
    public void send(BufferedWriter out, String message) throws IOException {
        out.write(message);
        out.flush();
    }
}
