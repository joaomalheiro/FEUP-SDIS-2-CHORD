package messages;

public class MessageForwarder {

    public static void sendMessage(Message message){
        Thread th = new Thread(new SendMessage(message));
        th.start();
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
