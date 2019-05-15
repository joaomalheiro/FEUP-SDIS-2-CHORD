package com.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        if (args.length != 4 && args.length != 5)
            return;

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket socket;
        PrintWriter out;
        BufferedReader in;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(IOException e){
            System.err.println("Error setting up socket or setting up streams");
            return;
        }

        String str = args[2] + " " + args[3];
        if(args.length == 5)
            str += " " + args[4];

        out.println(str);
        try {
            String answer = null;
            while(answer == null)
                answer = in.readLine();
            System.out.println("Received packet: " + answer);
            socket.close();
            out.close();
            in.close();
        } catch(IOException e){
            System.err.println("Error receiving packet or closing socket");
        }
    }
}
