package peer;

import chord.ChordInfo;
import messages.Auxiliary;

public class SuccessorRequest implements Runnable{

    private int referencedPort;
    private int port;

    public SuccessorRequest(int referencedPort, int port)
    {
        this.referencedPort = referencedPort;
        this.port = port;
    }

    @Override
    public void run() {

        String [] params = new String[]{String.valueOf(ChordInfo.peerHash), String.valueOf(this.port)};
        String sentence = Auxiliary.addHeader("GETSUCCESSOR", params);

        Auxiliary.sendMessage(sentence, "localhost", this.referencedPort);

        //Provavelmente vai ser preciso fazer algo que controle a chegada a resposta
    }
}
