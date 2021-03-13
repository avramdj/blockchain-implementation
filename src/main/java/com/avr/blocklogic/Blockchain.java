package com.avr.blocklogic;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Blockchain {

    private static final Logger logger = Logger.getLogger(Block.class.getName());

    private List<Block> chain;

    public Blockchain() {
        chain = new LinkedList<>();
    }

    public void add(Block b){
        chain.add(b);
    }

    public boolean isValidOrder(){
        if(chain.size() == 0) { return true; }
        String previousHash = chain.get(0).getPreviousHash();
        for(Block b : chain){
            if(b.getPreviousHash() != previousHash){
                return false;
            }
            logger.info("Verified " + b.getHash());
            previousHash = b.getHash();
        }
        return true;
    }

    public Block getLast(){
        return chain.get(chain.size()-1);
    }

    public void loadLedger() {
        //TODO
    }

    public String getLastHash() {
        return getLast().getHash();
    }
}