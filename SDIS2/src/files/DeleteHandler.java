package files;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;


import chord.ChordManager;
import chord.ConnectionInfo;
import messages.BackupMessage;
import messages.MessageForwarder;

public class DeleteHandler implements Runnable{
	private ConnectionInfo ci;
	private BigInteger hashfile;
	private int repDegree;
	private byte[] content;
	
	public DeleteHandler(ConnectionInfo ci,BigInteger hashfile,int repDegree,byte[] content) {
		this.ci = ci;
		this.hashfile = hashfile;
		this.repDegree = repDegree;
		this.content = content;
	}
	@Override
	public void run() {
		for(int i = 0; i < 4;i++){
			BackupMessage message = new BackupMessage(ci, hashfile, repDegree, content, ChordManager.getFingerTable().get(0).getIp(), ChordManager.getFingerTable().get(0).getPort());
			MessageForwarder.sendMessage(message);
			try {
				TimeUnit.SECONDS.sleep(4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
