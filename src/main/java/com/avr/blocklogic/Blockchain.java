package com.avr.blocklogic;

import com.avr.util.AsyncLogger;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class Blockchain implements Serializable{

    private static final int diffCycleDuration = 2021;
    private static final int initDifficulty = 5;

    private static final AsyncLogger logger = new AsyncLogger(Block.class.getSimpleName());
    private static final Level miningLogLevel = Level.INFO;
    private Signal stopCondition;

    private List<Block> blocks;

    public Blockchain() {
        blocks = new LinkedList<>();
        blocks.add(Block.genesisBlock());
    }

    public void add(Block b){
        blocks.add(b);
    }

    public boolean isValidOrder(){
        if(blocks.size() == 0) { return true; }
        String previousHash = blocks.get(0).getPreviousHash();
        for(Block b : blocks){
            if(b.getPreviousHash() != previousHash){
                return false;
            }
            logger.log(miningLogLevel, "Verified " + b.getHash());
            previousHash = b.getHash();
        }
        return true;
    }

    public Block makeBlock(List<Transaction> transactions, Signal stopCondition) throws BlockInterrupt {
        Block blk = new Block(size(), transactions, getLastHash());
        int difficulty = calcDifficulty();
        String prefixString = new String(new char[difficulty]).replace('\0', '0');
        while (!blk.getHash().substring(0, difficulty).equals(prefixString)) {
            Optional<Block> optBlock = stopCondition.poll();
            if(optBlock.isPresent()){
                Block interruptBlock = optBlock.get();
                if(interruptBlock.getId() >= blk.getId() /*&& b.verifyBlock()*/){
                    throw new BlockInterrupt(interruptBlock);
                }
            }
            blk.nextHash();
            //logger.log(miningLogLevel, "Mining :: nonce: " + blk.getNonce() + " hash: " + blk.getHash());
        }
        blocks.add(blk);
        return blk;
    }

    public void makeBlock(List<Transaction> transactions) {
        Block b = new Block(size(), transactions, getLastHash(), calcDifficulty());
        b.mineBlock();
        blocks.add(b);
    }

    private int calcDifficulty(){
        return (int) (size()/diffCycleDuration) + initDifficulty;
    }

    public Block getLast(){
        return blocks.get(blocks.size()-1);
    }

    public synchronized static Blockchain loadLedger(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fout = new FileInputStream(filename);
        ObjectInputStream oos = new ObjectInputStream(fout);
        return (Blockchain) oos.readObject();
    }

    public synchronized static void saveLedger(Blockchain bc, String filename) throws IOException {
        FileOutputStream fout = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(bc);
    }

    public void setStopCondition(Signal stopCondition) {
        this.stopCondition = stopCondition;
    }

    public String getLastHash() {
        return getLast().getHash();
    }

    public long size(){
        return blocks.size();
    }

    @Override
    public String toString() {
        return "Blockchain[[" +
                "Blocks=" + blocks +
                "]]";
    }
}