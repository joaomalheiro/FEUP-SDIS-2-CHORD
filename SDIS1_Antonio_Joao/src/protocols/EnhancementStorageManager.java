package protocols;

import messages.Message;
import messages.MessageController;
import peer.Peer;

import java.io.File;
import java.io.IOException;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static peer.Peer.getMC;

public class EnhancementStorageManager implements Runnable {

    @Override
    public void run() {
        while (true) {
            long wait_time = (long) (Math.random() * (50000 - 100)) + 100;
            try {
                TimeUnit.MILLISECONDS.sleep(wait_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Map<String,HashSet<Integer>> map = Peer.getMC().getRepDegreeStorage().getRepDegreeHashMap();
            Iterator<Map.Entry<String,HashSet<Integer>>> entries = map.entrySet().iterator();
            while(entries.hasNext()) {
                Map.Entry<String,HashSet<Integer>> entry = entries.next();
                String key = entry.getKey();
                String key2 = key.replace("fileId", "");
                String chnkn = key2.split("chkn")[1];
                key2 = key2.split("chkn")[0];
                File f = new File("./peerDisk/peer" + Peer.getPeerId() + "/backup/" + key2 + "/chk" + chnkn);
                if(f.exists() && !f.isDirectory()) {
                    Chunk chunk = null;
                    try {
                        chunk = MessageController.loadChunk(key2, Integer.parseInt(chnkn));
                    } catch (IOException ignored) {

                    } catch (ClassNotFoundException ignored) {

                    }
                    Peer.getMC().getRepDegreeStorage().removeChunkReplication(key2,Integer.parseInt(chnkn),Integer.parseInt(Peer.getPeerId()),chunk.getData());
                    if(Peer.getMC().getRepDegreeStorage().getDesiredRepDegree(key2) < Peer.getMC().getRepDegreeStorage().getRepDegree(key)){
                        File chunkf = new File("./peerDisk/peer" + Peer.getPeerId() + "/backup/" + key2 + "/chk" + chnkn);
                        deleteFile(chunkf);
                    }
                }

            }
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
                        //recursive delete
                        deleteFile(fileDelete);

                }

                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        }else{
                handleDeleteFile(file);
                //if file, then delete it
                file.delete();
                System.out.println("File is deleted : " + file.getAbsolutePath());

        }
    }

    private void handleDeleteFile(File file)  {
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
            getMC().getRepDegreeStorage().removeChunkReplication(fileId,chunkNumber,Integer.parseInt(Peer.getPeerId()),null);
            Message deleteMsg = new Message(Peer.getProtocolVersion(), Integer.parseInt(Peer.getPeerId()), fileId, chunkNumber, 0, null);
            deleteMsg.createRemoved();

        }

    }
}
