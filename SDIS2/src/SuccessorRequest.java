public class SuccessorRequest implements Runnable{

    private int referencedPort;
    private int port;

    SuccessorRequest(int referencedPort, int port)
    {
        this.referencedPort = referencedPort;
        this.port = port;
    }

    @Override
    public void run() {

        String [] params = new String[]{ChordInfo.peerHash, String.valueOf(this.port)};
        String sentence = Auxiliary.addHeader("GETSUCCESSOR", params);

        Auxiliary.sendMessage(sentence, "localhost", String.valueOf(this.referencedPort));

        //Provavelmente vai ser preciso fazer algo que controle a chegada a resposta
    }
}
