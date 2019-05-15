package protocols;


import messages.Message;
import peer.Peer;

import java.io.File;
import java.util.Hashtable;

public class Storage {
    private long spaceReserved;
    private long spaceOcupied;
    private Hashtable<Integer,Integer> initBackupChunks;

    public Storage(long spaceReserved){
        this.spaceReserved = spaceReserved;
        updateSpaceOcupied();
        if(this.spaceReserved < this.spaceOcupied){
            clearStorageSpace();
        }

        initBackupChunks = new Hashtable<>();
    }

    public void insertHashtable(Integer chunkId, Integer repDegree) {
        initBackupChunks.put(chunkId, repDegree);
    }

    public long getSpaceReserved() {
        return spaceReserved;
    }

    public long getSpaceOcupied() {
        return spaceOcupied;
    }

    public void updateSpaceOcupied(){
        File peerFolder = new File("./peerDisk/peer" + Peer.getPeerId());

        this.spaceOcupied = folderSize(peerFolder) / 1024;
    }

    private long folderSize(File directory) {
        long length = 0;
        try {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += folderSize(file);
            }
        }
        catch (NullPointerException e) {
        }
        return length;
    }

    public boolean allowChunk(byte[] chunkData){
        updateSpaceOcupied();
        return spaceOcupied + (chunkData.length / 1024) < spaceReserved;
    }

    public void setSpaceReserved(long spaceReserved) {
        this.spaceReserved = spaceReserved;
        updateSpaceOcupied();
        if(this.spaceReserved < this.spaceOcupied){
            clearStorageSpace();
        }
    }

    private void clearStorageSpace() {
        File peerFolder = new File("./peerDisk/peer" + Peer.getPeerId());

            try {
                for (File file : peerFolder.listFiles()) {
                    deleteFile(file);
                }
            }
            catch (NullPointerException e) {
            }
    }

    private void deleteFile(File file) {
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
                    if(this.spaceReserved < this.spaceOcupied){
                        //recursive delete
                        deleteFile(fileDelete);
                    }
                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        }else{
            if(this.spaceReserved < this.spaceOcupied) {
                handleDeleteFile(file);
                //if file, then delete it
                file.delete();
                System.out.println("File is deleted : " + file.getAbsolutePath());
                updateSpaceOcupied();
            }
        }
    }

    private void handleDeleteFile(File file) {
        String absolutePath = file.getAbsolutePath();
        String fileId;
        int chunkNumber;

       String[] path;
        if(System.getProperty("os.name").equals("Linux")){
            path = absolutePath.split("/");
        } else {
            path = absolutePath.split("\\\\");
        }

        if(path[path.length - 1].contains("chk")){
            fileId = path[path.length - 2];
            chunkNumber = Integer.parseInt(path[path.length - 1].replace("chk",""));
            //System.out.println(fileId + " " + chunkNumber);
            Message deleteMsg = new Message(Peer.getProtocolVersion(), Integer.parseInt(Peer.getPeerId()), fileId, chunkNumber, 0, null);
            deleteMsg.createRemoved();

        }

    }

    public Hashtable<Integer, Integer> getHashtable() {
        return initBackupChunks;
    }
}
