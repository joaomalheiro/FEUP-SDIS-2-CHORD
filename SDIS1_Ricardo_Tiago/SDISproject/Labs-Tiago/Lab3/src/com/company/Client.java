package com.company;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {
        if (args.length != 4 && args.length != 5)
            return;

        Integer host = Integer.parseInt(args[0]);
        String response = "";
        String remote = args[1];
        String str = args[2] + " " + args[3];
        if(args.length == 5)
            str += " " + args[4];

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            Plate stub = (Plate) registry.lookup(remote);
            response = stub.operation(str);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        for (int i = 2; i < args.length; i++){
            System.out.print("<" + args[i] + "> ");
        }

        System.out.print(":: <" + response + ">\n");
    }
}
