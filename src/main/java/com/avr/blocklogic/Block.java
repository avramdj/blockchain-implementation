package com.avr.blocklogic;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Block {
    private String hash;
    private final String previousHash;
    private final String data;
    private final long timeStamp;
    private final int difficulty = 2;
    private int nonce;

    private static final Logger logger = Logger.getLogger(Block.class.getName());

    public Block(Collection<Transaction> dataCollection, String previousHash, long timeStamp) {
        StringBuilder data = new StringBuilder();
        for(Transaction t : dataCollection){
            data.append(t.toString());
        }
        this.data = data.toString();
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = computeHash();
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

    public String mineBlock() {
        String prefixString = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(prefixString)) {
            nonce++;
            hash = computeHash();
            logger.log(Level.INFO, "Mining :: nonce: " + nonce + " hash: " + hash);
        }
        return hash;
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
}