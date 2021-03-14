package com.avr.blocklogic;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Block {
    private long id;
    private String hash;
    private final String previousHash;
    private final String data;
    private final int difficulty;
    private final long timeStamp;
    private int nonce = 0;

    private boolean STOP_SIGNAL = false;

    private static final Logger logger = Logger.getLogger(Block.class.getName());
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
                + Integer.toString(nonce)
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

    public static Block mineBlock(Block blk, Signal stopCondition) {
        String prefixString = new String(new char[blk.difficulty]).replace('\0', '0');
        while (!blk.hash.substring(0, blk.difficulty).equals(prefixString)) {
            Block b = stopCondition.poll();
            if(b != null){
                if(b.getId() >= blk.getId() /*&& b.verifyBlock()*/){
                    return b;
                } else {
                    stopCondition.pop();
                }
            }
            blk.nonce++;
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
            nonce++;
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

    public Object getNonce() {
        return nonce;
    }
}