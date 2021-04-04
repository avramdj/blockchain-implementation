package com.avr.blocklogic;

import com.avr.mockservices.TransactionGenerator;
import com.avr.util.AsyncLogger;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class Block implements Serializable {
    private long id;
    private String hash;
    private final List<Transaction> transactions;
    private final String previousHash;
    private final String serializedTransactions;
    private final int difficulty;
    private final int transactionCount;
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
        this.transactions = transactionList;
        this.serializedTransactions = data.toString();
        this.transactionCount = transactionList.size();
        this.previousHash = previousHash;
        this.difficulty = difficulty;
        this.timeStamp = TimeServer.time();
        this.hash = computeHash();
        this.id = id;
        this.random = new Random();
    }

    public Block(long id, List<Transaction> transactionList, String previousHash) {
        StringBuilder data = new StringBuilder();
        if(transactionList != null){
            for(Transaction t : transactionList){
                data.append(t.toString());
            }
        }
        this.transactions = transactionList;
        this.serializedTransactions = data.toString();
        this.transactionCount = transactionList.size();
        this.previousHash = previousHash;
        this.difficulty = 0;
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
                + serializedTransactions;
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

    public String nextHash() {
        nextNonce();
        this.hash = computeHash();
        return this.hash;
    }

    private static Block mineBlock(Block blk, Signal stopCondition) throws BlockInterrupt {
        String prefixString = new String(new char[blk.difficulty]).replace('\0', '0');
        while (!blk.hash.substring(0, blk.difficulty).equals(prefixString)) {
            Optional<Block> optBlock = stopCondition.poll();
            if(optBlock.isPresent()){
                Block interruptBlock = optBlock.get();
                if(interruptBlock.getId() >= blk.getId() /*&& b.verifyBlock()*/){
                    throw new BlockInterrupt(interruptBlock);
                }
            }
            blk.nonce = abs(blk.random.nextLong());
            blk.hash = blk.computeHash();
            logger.log(miningLogLevel, "Mining :: nonce: " + blk.nonce + " hash: " + blk.hash);
        }
        return blk;
    }

    public long getId() {
        return id;
    }

    public String mineBlock() {
        String prefixString = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(prefixString)) {
            nonce = abs(random.nextLong());
            hash = computeHash();
            logger.log(Level.INFO, "Mining :: nonce: " + nonce + " hash: " + hash + '\n');
        }
        return hash;
    }

    public static Block genesisBlock(){
        return new Block(0, TransactionGenerator.getNextList(1), "0", 1);
    }

    public boolean verifyTransactions(){
        //TODO
        return true;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    private long nextNonce() {
        this.nonce = abs(random.nextLong());
        return this.nonce;
    }

    @Override
    public String toString() {
        return "Block[" +
                "hash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                ", data='" + serializedTransactions + '\'' +
                ", timeStamp=" + timeStamp +
                ", difficulty=" + difficulty +
                ", nonce=" + nonce +
                ']';
    }

    public Long getNonce() {
        return nonce;
    }
}