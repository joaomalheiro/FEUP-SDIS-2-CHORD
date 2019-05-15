package channel;

import files.ResponseHandler;
import messages.Message;
import peer.Peer;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RepDegreeStorage implements Serializable{
    private Map<String,HashSet<Integer>> repDegree = new ConcurrentHashMap<>();
    private Map<String,Integer> desiredRepDegree = new ConcurrentHashMap<>();
    private HashSet<Message> deleteMessages = new HashSet<>();

    public void saveChunkReplication(String fileId,int chunkNumber,int peerId){
        String key = "";

        key = "fileId" + fileId + "chkn" + chunkNumber;

        if (!repDegree.containsKey(key)){
            repDegree.put(key, new HashSet<>());
        }
        repDegree.get(key).add(peerId);
        this.saveRepDegreeStorage();

    }

    public void removeChunkReplication(String fileId,int chunkNumber,int peerId,byte[] data){
        String key = "fileId" + fileId + "chkn" + chunkNumber;
        if (repDegree.containsKey(key)){
            repDegree.get(key).remove(peerId);
            if(getRepDegree(key) < getDesiredRepDegree(fileId)){
                long wait_time = (long) (Math.random() * (400 - 1)) + 1;
                try {
                    TimeUnit.MILLISECONDS.sleep(wait_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(getRepDegree(key) < getDesiredRepDegree(fileId)) {
                    Message msg = new Message(Peer.getProtocolVersion(), Integer.parseInt(Peer.getPeerId()),  fileId, chunkNumber, getDesiredRepDegree(fileId), data);
                    ResponseHandler resp = new ResponseHandler(getDesiredRepDegree(fileId), key,msg);
                    new Thread(resp).start();
                }
            }
        }
        this.saveRepDegreeStorage();

    }
    public int getRepDegree(String key){

        if(repDegree.containsKey(key))
            return repDegree.get(key).size();
        return 0;
    }

    public boolean existsRepDegree(String key){
        return repDegree.containsKey(key);
    }

    public int getDesiredRepDegree(String key){
        return desiredRepDegree.get(key);
    }

    public void setDesiredRepDegree(String key, int replicationDegree){
        desiredRepDegree.put(key, replicationDegree);
        this.saveRepDegreeStorage();
    }

    public void saveRepDegreeStorage(){
        try {
            FileOutputStream fileOut = new FileOutputStream("./storage" + Peer.getPeerId());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void addDeleteMessage(Message msg){
        deleteMessages.add(msg);
    }
    public HashSet<Message> getDeleteMessages(){
        return this.deleteMessages;
    }
    public void removeDeleteMessage(Message msg){
        if(deleteMessages.contains(msg)){
            deleteMessages.remove(msg);
        }
    }

    public void sendAllDeleteMessages(){
        for (Message m : deleteMessages) {
            m.sendDelete();
        }
    }

    public Map<String,Integer> getDesiredHashMap(){
        return desiredRepDegree;
    }
    public Map<String,HashSet<Integer>> getRepDegreeHashMap(){
        return repDegree;
    }
}
