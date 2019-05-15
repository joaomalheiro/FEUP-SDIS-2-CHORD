package messages;

import peer.Peer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Message implements Serializable {
	
	private String version;
	private int senderId;
	private String fileId;
	private int chunkNumber;
	private int replicationDeg;
	private byte[] body;

	/**
	 * Marks the ending of the header part of the message : <CRLF><CRLF>
	 */
	public static String messageEnd = " " + (char) 0xD + (char) 0xA + (char) 0xD + (char) 0xA;

	/**
	 * Constructor for the class Message. It is the class responsible for creating the byte[] that will be sent through the MultiCast channels
	 * @param version
	 * @param senderId
	 * @param fileId
	 * @param chunkNumber
	 * @param replicationDeg
	 * @param body
	 */
	public Message(String version, int senderId, String fileId, int chunkNumber, int replicationDeg, byte[] body){
		this.version = version;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNumber = chunkNumber;
		this.replicationDeg = replicationDeg;
		this.body = body;
	}

	/**
	 * Encrypts a string(text) that is the filename and returns the new hashed fileId
	 * @param text
	 * @return String
	 */
	public static String encrypt(String text) {

		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(encodedhash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "error in hashing";
	}

	/**
	 * Helper that passes a hashed byte[] into a string
	 * @param hash
	 * @return
	 */
	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if(hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Creates putchunk message
	 * PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
	 * @return String[]
	 */
	public synchronized String[] createPutChunk() {
		String header = new String("PUTCHUNK" + " " + this.version + " " + this.senderId + " " + encrypt(this.fileId) + " " + this.chunkNumber + " " + this.replicationDeg + messageEnd);
		ByteArrayOutputStream outputMessageStream = new ByteArrayOutputStream();

		try {
			outputMessageStream.write(Arrays.copyOf(header.getBytes(), header.length()));
			if(this.body != null)
				outputMessageStream.write(Arrays.copyOf(this.body, this.body.length));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Peer.getMC().getRepDegreeStorage().setDesiredRepDegree(this.fileId,this.replicationDeg);
		Peer.getMDB().sendMsg(outputMessageStream.toByteArray());
		return header.split(" ");
	}

	/**
	 * Creates Stored Message
	 * STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
	 * @return
	 */
	public byte[] createStored() {

		String header = "STORED" + " " + this.version + " " + this.senderId + " " + this.fileId + " " + this.chunkNumber + messageEnd;

		return header.getBytes();
	}

	/**
	 * Creates getchunk message
	 * GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
	 */
	public void createGetChunk() {

		String header = "GETCHUNK" + " " + this.version + " " + this.senderId + " " + encrypt(this.fileId) + " " + this.chunkNumber + messageEnd;

		Peer.getMC().sendMsg(header.getBytes());
	}

	/**
	 * Creates chunk message
	 * CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
	 * @return byte[]
	 */
	public byte[] createChunk() {

		String header = "CHUNK" + " " + this.version + " " + this.senderId + " " + this.fileId + " " + this.chunkNumber + messageEnd;
		ByteArrayOutputStream outputMessageStream = new ByteArrayOutputStream();

		try {
			outputMessageStream.write(Arrays.copyOf(header.getBytes(), header.length()));
			outputMessageStream.write(Arrays.copyOf(this.body, this.body.length));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outputMessageStream.toByteArray();
	}

	/**
	 * Creates delete message
	 * DELETE <Version> <SenderId> <FileId> <CRLF><CRLF>
	 */
	public void createDelete() {

		String header = new String("DELETE" + " " + this.version + " " + this.senderId + " " + encrypt(this.fileId) + messageEnd);

		Peer.getMC().sendMsg(header.getBytes());
 	}

 	public void sendDelete() {
		String header = new String("DELETE" + " " + this.version + " " + this.senderId + " " + this.fileId + messageEnd);

		Peer.getMC().sendMsg(header.getBytes());
	}
	

	/**
	 * Created removed message
	 * REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
	 */
	public void createRemoved() {

		String header = "REMOVED" + " " + this.version + " " + this.senderId + " " + this.fileId + " " + this.chunkNumber + messageEnd;

		Peer.getMC().sendMsg(header.getBytes());
	}
	
	public String getFileId() {
		return this.fileId;
	}
	
	
	
}
