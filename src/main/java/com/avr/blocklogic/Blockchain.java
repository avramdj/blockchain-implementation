package com.avr.blocklogic;

import com.avr.util.AsyncLogger;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class Blockchain {

    private static final int diffCycleDuration = 2021;
    private static final int initDifficulty = 5;

    private static final AsyncLogger logger = new AsyncLogger(Block.class.getSimpleName());
    private static final Level miningLogLevel = Level.INFO;

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

    public Block makeBlock(List<Transaction> transactions, Signal stopCondition) throws StoppedException {
        Block b = new Block(size(), transactions, getLastHash(), calcDifficulty());
        b = Block.mineBlock(b, stopCondition);
        blocks.add(b);
        return b;
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

    public void loadLedger() {
        //TODO
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