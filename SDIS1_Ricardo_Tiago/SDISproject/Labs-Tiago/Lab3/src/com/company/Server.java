package com.company;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;


public class Server implements Plate{
    private static Hashtable<String, String> db = new Hashtable<>();

    public String operation(String str){
        String answer;

        String[] args = str.split(" ");

        System.out.print("<" + args[0] + "> ");

        for (int i = 1; i < args.length; i++){
            System.out.print("<" + args[i] + "> ");
        }

        if (args[0].equals("REGISTER") && args.length == 3){
            db.put(args[1], args[2]);
            answer = ""+db.size();
        } else if (args[0].equals("LOOKUP") && args.length == 2){
            answer = db.get(args[1].replaceAll("\0", ""));
            if(answer == null) answer = "-1";
        } else {
            answer = "-1";
        }

        System.out.print(":: <" + answer + ">\n");

        return answer;
    }

    public static void main(String[] args) {
        if(args.length != 1)
            return;

        String remote = args[0];

        try {
            Server obj = new Server();
            Plate stub = (Plate) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.createRegistry(1009);
            registry.bind(remote, stub);
            System.out.println("Remote object set up");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
