package messages;

public class MessageForwarder {

    public static void sendMessage(Message message){
        System.out.println("beggining of send message");

        Thread th = new Thread(new SendMessage(message));
        th.start();
        //SendMessage sm = new SendMessage(message);
        //Peer.executor.submit(sm);
    }

    public static String addHeader(String type, String[] params) {
        StringBuilder result = new StringBuilder();

        for (String param : params) {
            result.append(param);
            result.append(" ");
        }

        return type + " " +
                result.toString() +
                "\r\n\r\n";
    }
}
