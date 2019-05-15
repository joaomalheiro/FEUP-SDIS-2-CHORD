package com.code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Hashtable;


public class Server implements Runnable{
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Hashtable<Integer, String> db = new Hashtable<>();

    public void run() {
        String str = "";

        while(!str.equals("STOP")){
            str = "";

            try {
                socket = serverSocket.accept();
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch(IOException e){
                System.err.println("Error creating socket or setting up streams");
                return;
            }

            try {
                while(str == null || str.equals(""))
                    str = in.readLine();
                System.out.println("Received packet");
            } catch(IOException e){
                System.err.println("Error receiving packet");
                return;
            }

            String[] tokens = str.split(" ");
            String answer;

            if (tokens[0].equals("REGISTER") && tokens.length == 3){
                System.out.println("<" + tokens[0] + "> <" + tokens[1] + "> <" + tokens[2].replaceAll("\0", "") + ">");
                db.put(Integer.parseInt(tokens[1]), tokens[2]);
                answer = ""+db.size();
            } else if (tokens[0].equals("LOOKUP") && tokens.length == 2){
                System.out.println("<" + tokens[0] + "> <" + tokens[1].replaceAll("\0", "") + ">");
                answer = db.get(Integer.parseInt(tokens[1].replaceAll("\0", "")));
                if(answer == null) answer = "-1";
            } else {
                System.out.println("<ERROR>");
                answer = "-1";
            }

            out.println(answer);
        }

        try {
            socket.close();
        } catch(IOException e){
            System.err.println("Error closing socket");
        }
    }

    public static void main(String[] args) {
        if(args.length != 1)
            return;

        int port = Integer.parseInt(args[0]);

        try {
            serverSocket = new ServerSocket(port);
        } catch(IOException e){
            System.err.println("Error creating server socket");
            return;
        }

        Thread th = new Thread(new Server());
        System.out.println("Created thread");
        try {
            th.join();
        } catch (InterruptedException e){
            System.err.println("Error waiting for thread");
            try{socket.close();}catch(IOException e2){
                System.err.println("Error closing socket");}
            return;
        }
        th.start();
    }
}
