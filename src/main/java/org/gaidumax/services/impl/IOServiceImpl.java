package org.gaidumax.services.impl;

import org.gaidumax.services.interfaces.IOService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class IOServiceImpl implements IOService {

    @Override
    public String read(BufferedReader in) throws IOException {
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

    @Override
    public void send(BufferedWriter out, String message) throws IOException {
        out.write(message);
        out.flush();
    }
}
