package messages;

import messages.Message;
import peer.Peer;
import protocols.Chunk;

import java.io.*;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MessageController implements Runnable {
	private DatagramPacket packet;
	private String[] header;

    /**
     * Constructor for MessageController class. It is the class called when a new packet is received
     * @param packet
     */
	public MessageController(DatagramPacket packet) {
		this.packet = packet;
	}

	@Override
	public void run() {
		header = parsePacketHeader();

		String type = header[0];
	
		//If message comes from the same peer
		if(header[2].equals(Peer.getPeerId())) {
			System.out.println("Message from me");
			return;
		}
		
		switch(type) {
			case "PUTCHUNK":
			    if(Peer.getStorage().allowChunk(packet.getData())){
                    handlePutChunk();
                } else {
			        System.out.println("Not enough space");
                }

				break;
			case "STORED":
				handleStored();
				break;
			case "GETCHUNK":
				handleGetChunk();
				break;
			case "CHUNK":
				System.out.println("CHUNK");
				handleChunk();
				break;
            case "DELETE":
                handleDelete();
                break;
            case "REMOVED":
                handleRemoved();
                break;
			case "JOINED":
				handleJoined();
				break;
			default:
				break;
		}
		
	}

    /**
     * Handles a removed message received in the MultiCast Channel
     */
	private void handleJoined() {
		if(header[1].equals("3.0")) {
			Peer.getMC().getRepDegreeStorage().sendAllDeleteMessages();
		}
	}

	private void handleRemoved() {
        String fileId = header[3];
        int chunkNumber = Integer.parseInt(header[4]);
	    System.out.println(header[0] + " " + header[1] + " " + header[2] + " " + header[3]);
        File f = new File("./peerDisk/peer" + Peer.getPeerId() + "/backup/" + fileId + "/chk" + chunkNumber);
        if(f.exists() && !f.isDirectory()) {
            Chunk chunk = null;
            try {
                chunk = loadChunk(fileId, chunkNumber);
            } catch (IOException ignored) {

            } catch (ClassNotFoundException ignored) {

            }
           Peer.getMC().getRepDegreeStorage().removeChunkReplication(fileId,chunkNumber,Integer.parseInt(header[2]),chunk.getData());
        }
    }

    /**
     * Handles a delete message received in the MultiCast Channel
     */
    private void handleDelete() {

        String fileId = header[3];

        File directory = new File("./peerDisk/peer" + Peer.getPeerId() + "/backup/" + fileId);
		System.out.println(header[0] + " " + header[1] + " " + header[2] + " " + header[3]);
		/*if(header[1].equals("3.0")) {
			Message msg = new Message(Peer.getProtocolVersion(), Integer.parseInt(Peer.getPeerId()), fileId, 0, 0, null);
			Peer.getMC().getRepDegreeStorage().addDeleteMessage(msg);
		}*/

        if(!directory.exists()){

            System.out.println("Directory does not exist.");

        }else{
            delete(directory);
        }
    }

    /**
     * Deletes a file passed as parameter
     * @param file
     */
    private void delete(File file) {
        if(file.isDirectory()){

            //directory is empty, then delete it
            if(file.list().length==0){

                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());

            }else{

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    delete(fileDelete);
                }
				try{
                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
				} catch(Exception ignore){
				
				}
				
            }

        }else{
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }

    /**
     * Handles a stored message received in the MultiCast Channel
     */
    private void handleStored() {

		String version = header[1];
		int senderId = Integer.parseInt(header[2]);
		String fileId = header[3];
		int chunkNumber = Integer.parseInt(header[4]);
        System.out.println("STORED " + version + " " + senderId + " " + fileId + " " + chunkNumber);
		Peer.getMC().getRepDegreeStorage().saveChunkReplication(fileId, chunkNumber, senderId);
	}

    /**
     * Handles a putchunk message received in the MultiCast Channel
     */
	private void handlePutChunk() {
		String fileId = header[3];
		int chunkNumber = Integer.parseInt(header[4]);
		int replicationDeg = Integer.parseInt(header[5]);
        System.out.println("PUTCHUNK " + header[2] + " " + fileId + " " + chunkNumber + " " + replicationDeg);

       /* if(header[1].equals("2.0")){
            if(Peer.getMC().getRepDegreeStorage().getDesiredRepDegree(fileId) <= Peer.getMC().getRepDegreeStorage().getRepDegree("fileId" + fileId + "chkn" + chunkNumber)){
                return;
            }
        }*/
		Chunk chunk = new Chunk(fileId,chunkNumber,replicationDeg, getDataFromPacket());

		String fileIdDir = "peerDisk/peer" + Peer.getPeerId() + "/backup/" + fileId;
		new File("./" + fileIdDir).mkdirs();

		saveChunk(chunk, fileId);

		Chunk newChunk = null;
		try {
			newChunk = loadChunk(fileId,  chunkNumber);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		String s = new String(newChunk.getData());

		Peer.getMC().getRepDegreeStorage().saveChunkReplication(fileId, chunkNumber, Integer.parseInt(Peer.getPeerId()));
        Peer.getMC().getRepDegreeStorage().setDesiredRepDegree(fileId,replicationDeg);
		sendStored(fileId, chunkNumber, replicationDeg);
	}

    /**
     * Parsed the body of the message and returns it as a byte[]
     * @return
     */
	private byte[] getDataFromPacket() {
		
		int headerLength = 0;
		
		for(int i = 0; i < packet.getData().length; i++){

            if((packet.getData()[i] == (char)0xD) &&(packet.getData()[i+1] == (char)0xA)&&(packet.getData()[i] == (char)0xD)&&(packet.getData()[i+1] == (char)0xA)){
				break;
			}
			headerLength++;
		}

		return Arrays.copyOfRange(packet.getData(), headerLength + 4, packet.getLength());
		
	}

    /**
     * Handles a getchunk message received in the MultiCast Channel
     */
	private void handleGetChunk(){
		String fileId = header[3];
		int chunkNumber = Integer.parseInt(header[4]);
		System.out.println(header[0] + " " + fileId + " " + chunkNumber);

		try {
            Chunk chunk = loadChunk(fileId, chunkNumber);
			sendChunk(chunk);
		} catch (IOException e) {
			System.out.println("Do not own that chunk");
		} catch (ClassNotFoundException e) {
			System.out.println("Do not own that chunk");
		}
	}

    /**
     * Handles a chunk message received in the MultiCast Channel
     */
	private void handleChunk() {
		String fileId = header[3];
		int chunkNumber = Integer.parseInt(header[4]);
		byte[] data = getDataFromPacket();

		//System.out.println(fileId);
		//System.out.println(chunkNumber);
		//System.out.println("Received Chunk with data :");
		//System.out.println(new String(data));
		//System.out.println(data.length);

		Chunk chunk = new Chunk(fileId,chunkNumber, 0, data);
		Peer.getMDR().insertChunk(chunk, fileId);

	}

    /**
     * Sends a chunk through the MDR channel
     * @param chunk
     */
	public static void sendChunk(Chunk chunk){
		Message responseMsg = new Message(Peer.getProtocolVersion(), Integer.parseInt(Peer.getPeerId()), chunk.getFileId(), chunk.getChunkNumber(), 0,chunk.getData());
		byte[] response = responseMsg.createChunk();

		try {
			Thread.sleep((long)(Math.random() * 400));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Peer.getMDR().sendMsg(response);
	}

    /**
     * Sends a stored message through the MC channel
     * @param fileId
     * @param chunkNumber
     * @param replicationDeg
     */
	private void sendStored(String fileId, int chunkNumber, int replicationDeg){

		Message responseMsg = new Message(Peer.getProtocolVersion(), Integer.parseInt(Peer.getPeerId()), fileId, chunkNumber, replicationDeg, null);
		byte[] response = responseMsg.createStored();

		try {
			Thread.sleep((long)(Math.random() * 400));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long wait_time = (long) (Math.random() * (400 - 1)) + 1;
		try {
			TimeUnit.MILLISECONDS.sleep(wait_time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Peer.getMC().sendMsg(response);

	}

    /**
     * Saves a chunk in the peer folder and file folder passed as parameter
     * @param chunk
     * @param fileId
     */
	private static void saveChunk(Chunk chunk, String fileId){
		try {
			FileOutputStream fileOut = new FileOutputStream("./peerDisk/peer" + Peer.getPeerId() + "/backup/" + fileId  + "/chk" + chunk.getChunkNumber() );
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(chunk);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

    /**
     * Loads a chunk from the filesystem of the peer
     * @param fileId
     * @param chunkNumber
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public static Chunk loadChunk(String fileId, int chunkNumber) throws IOException, ClassNotFoundException {

		Chunk chunk = null;
		FileInputStream fileIn = new FileInputStream("./peerDisk/peer" + Peer.getPeerId() + "/backup/" + fileId  + "/chk" + chunkNumber);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		chunk = (Chunk) in.readObject();
		in.close();
		fileIn.close();

		return chunk;
	}

    /**
     * Parses the header of a message
     * @return String[]
     */
	private String[] parsePacketHeader() {
		
		byte[] buffer;
		buffer = packet.getData();
		String headerString =new String(buffer, 0, packet.getLength());
		
		return headerString.split(" ");
	}
	
	
	

}
