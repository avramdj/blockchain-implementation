package com.avr.blocklogic;

import com.avr.util.AsyncLogger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class Block {
    private long id;
    private String hash;
    private final String previousHash;
    private final String data;
    private final int difficulty;
    private final long timeStamp;
    private long nonce = 0;

    private Random random;
    private boolean STOP_SIGNAL = false;

    private static final AsyncLogger logger = new AsyncLogger(Block.class.getSimpleName());
    private static Level miningLogLevel = Level.FINE;

    public Block(long id, List<Transaction> transactionList, String previousHash, int difficulty) {
        StringBuilder data = new StringBuilder();
        if(transactionList != null){
            for(Transaction t : transactionList){
                data.append(t.toString());
            }
        }
        this.data = data.toString();
        this.previousHash = previousHash;
        this.difficulty = difficulty;
        this.timeStamp = TimeServer.time();
        this.hash = computeHash();
        this.id = id;
        this.random = new Random();
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    private String computeHash() {
        String dataToHash = previousHash
                + Long.toString(timeStamp)
                + Long.toString(nonce)
                + data;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        StringBuilder buffer = new StringBuilder();
        assert bytes != null;
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public static Block mineBlock(Block blk, Signal stopCondition) throws StoppedException {
        String prefixString = new String(new char[blk.difficulty]).replace('\0', '0');
        while (!blk.hash.substring(0, blk.difficulty).equals(prefixString)) {
            Block b = stopCondition.poll();
            if(b != null){
                if(b.getId() >= blk.getId() /*&& b.verifyBlock()*/){
                    throw new StoppedException();
                } else {
                    stopCondition.pop();
                }
            }
            blk.nonce = abs(blk.random.nextLong());
            blk.hash = blk.computeHash();
            logger.log(miningLogLevel, "Mining :: nonce: " + blk.nonce + " hash: " + blk.hash);
        }
        return blk;
    }

    private long getId() {
        return id;
    }

    public String mineBlock() {
        String prefixString = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(prefixString)) {
            nonce = abs(random.nextLong());
            hash = computeHash();
            logger.log(Level.INFO, "Mining :: nonce: " + nonce + " hash: " + hash);
        }
        return hash;
    }

    public static Block genesisBlock(){
        return new Block(0, null, "0", 1);
    }

    public boolean verifyTransactions(){
        //TODO
        return true;
    }

    @Override
    public String toString() {
        return "Block[" +
                "hash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", data='" + data + '\'' +
                ", timeStamp=" + timeStamp +
                ", difficulty=" + difficulty +
                ", nonce=" + nonce +
                ']';
    }

    public Long getNonce() {
        return nonce;
    }
}