package com.company;

import java.io.IOException;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        if (args.length != 4 && args.length != 5)
            return;

        InetAddress address;
        DatagramSocket socket;

        try {
            address = InetAddress.getByName(args[0]);
            System.out.println("Created address");
        } catch(UnknownHostException e){
            System.err.println("Error creating address");
            return;
        }
        int port = Integer.parseInt(args[1]);

        try {
            socket = new DatagramSocket();
            System.out.println("Created socket");
        } catch(SocketException e){
            System.err.println("Error creating socket");
            return;
        }

        String str = args[2] + " " + args[3];
        if(args.length == 5)
            str += " " + args[4];
        byte[] buf = str.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

        try {
            socket.send(packet);
            System.out.println("Sent packet");
        } catch(IOException e){
            System.err.println("Error sending packet");
            return;
        }

        try {
            byte[] answerB = new byte[256];
            DatagramPacket answer = new DatagramPacket(answerB, answerB.length, address, port);
            socket.receive(answer);
            System.out.println("Received packet: " + (new String(answer.getData())).replaceAll("\0", ""));
        } catch(IOException e){
            System.err.println("Error receiving packet");
            return;
        }

        socket.close();
    }
}
