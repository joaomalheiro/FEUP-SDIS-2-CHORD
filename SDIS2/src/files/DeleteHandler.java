package files;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;


import chord.ChordManager;
import chord.ConnectionInfo;
import messages.BackupMessage;
import messages.ReclaimBackup;
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
		
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ReclaimBackup message = new ReclaimBackup(ci, hashfile, repDegree, content, ChordManager.getFingerTable().get(0).getIp(), ChordManager.getFingerTable().get(0).getPort());
			MessageForwarder.sendMessage(message);
	}

}
