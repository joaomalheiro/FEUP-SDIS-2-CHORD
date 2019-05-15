package com.company;

import java.io.IOException;
import java.net.*;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;


public class Server implements Runnable{
    private static DatagramSocket socket;
    private static MulticastSocket mcSocket;
    private static Hashtable<Integer, String> db = new Hashtable<>();
    private static int m_port;
    private static int s_port;
    private static InetAddress mcAddr;

    public void run() {
        String str = "";
        String answer;

        while(!str.equals("CLOSE")){
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
                System.out.println("Sent response packet");
            } catch(IOException e){
                System.err.println("Error sending response packet");
                return;
            }
        }

        socket.close();
    }

    static class Task extends TimerTask {
        public void run() {
            String port = "" + s_port;
            DatagramPacket packet = new DatagramPacket(port.getBytes(), port.getBytes().length, mcAddr, m_port);
            try {
                mcSocket.send(packet);
                System.out.println("Multicast packet sent");
            } catch (IOException e) {
                System.err.println("Multicast packet send failed");
            }
        }
    }

    public static void main(String[] args) {
        if(args.length != 3)
            return;

        s_port = Integer.parseInt(args[0]);
        String m_addr = args[1];
        m_port = Integer.parseInt(args[2]);

        try {
            socket = new DatagramSocket(s_port);
            System.out.println("Created socket");
        } catch (SocketException e){
            System.err.println("Error creating socket");
            return;
        }

        try {
            mcSocket = new MulticastSocket();
            mcSocket.setTimeToLive(1);
            System.out.println("Multicast socket set up successful");
        } catch (IOException e){
            System.err.println("Error setting up multicast socket");
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

        try {
            mcAddr = InetAddress.getByName(m_addr);
            System.out.println("Multicast address created");
        } catch (UnknownHostException e){
            System.err.println("Multicast address unknown");
            return;
        }

        Timer t = new Timer();
        t.scheduleAtFixedRate(new Task(), 0, 1000);
    }
}
