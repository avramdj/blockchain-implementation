package com.avr.network;

import com.avr.blocklogic.Block;

import java.util.*;

public class CentralBlockClient implements BlockClient {

    private Queue<Block> blkQueue;
    private final static int maxTransfer = 1;
    private static Set<CentralBlockClient> clients = new HashSet<>();

    public static void disconnect(BlockClient blClient) {
        clients.remove(blClient);
    }

    @Override
    public void broadcastBlock(Block blk) {
        serverBroadcastBlock(blk, this);
    }

    public CentralBlockClient() { }

    public static CentralBlockClient createConnection(){
        CentralBlockClient cbc = new CentralBlockClient();
        cbc.blkQueue = new LinkedList<>();
        clients.add(cbc);
        return cbc;
    }

    private static synchronized void serverBroadcastBlock(Block blk, BlockClient origin){
        for(CentralBlockClient bc : clients){
            if(bc != origin){
                bc.blkQueue.add(blk);
            }
        }
    }

    @Override
    public synchronized Block getBlock() {
        if(blkQueue.size() == 0){
            return null;
        }
        return blkQueue.peek();
    }

    @Override
    public synchronized Block pop() {
        return blkQueue.remove();
    }

    public void confirmBlock(Block blk){
        blkQueue.remove(blk);
    }
}
