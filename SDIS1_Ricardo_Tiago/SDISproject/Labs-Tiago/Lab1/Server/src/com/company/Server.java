package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;


public class Server implements Runnable{
    private static DatagramSocket socket;
    private static Hashtable<Integer, String> db = new Hashtable<>();

    public void run() {
        String str = "";
        String answer;

        while(!str.equals("STOP")){
            byte[] receiveBuf = new byte[256];
            DatagramPacket packet = new DatagramPacket(receiveBuf, receiveBuf.length);

            packet.setData(new byte[256]);
            try {
                socket.receive(packet);
                System.out.println("Received packet");
            } catch(IOException e){
                System.err.println("Error receiving packet");
                return;
            }

            str = new String(packet.getData());
            String[] tokens = str.split(" ");

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

            InetAddress address = packet.getAddress();
            int port = packet.getPort();


            byte[] sendBuf = answer.getBytes();
            packet = new DatagramPacket(sendBuf, sendBuf.length, address, port);

            try {
                socket.send(packet);
                System.out.println("Sent packet");
            } catch(IOException e){
                System.err.println("Error sending packet");
                return;
            }
        }

        socket.close();
    }

    public static void main(String[] args) {
        if(args.length != 1)
            return;

        int port = Integer.parseInt(args[0]);

        try {
            socket = new DatagramSocket(port);
            System.out.println("Created socket");
        } catch (SocketException e){
            System.err.println("Error creating socket");
            return;
        }

        Thread th = new Thread(new Server());
        System.out.println("Created thread");
        try {
            th.join();
        } catch (InterruptedException e){
            System.err.println("Error waiting for thread");
            socket.close();
            return;
        }
        th.start();
    }
}
