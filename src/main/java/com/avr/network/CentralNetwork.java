package com.avr.network;

import com.avr.blocklogic.Block;
import com.avr.blocklogic.Transaction;

import java.net.URL;

public class CentralNetwork implements NodeNetwork{
    private URL serverAddress;
    private int port;

    @Override
    public void broadcastBlock(Block b) {

    }

    @Override
    public void broadcastTransaction(Transaction t) {

    }

    @Override
    public void requestBlock(String hash) {

    }

    @Override
    public Block listenBlock() {
        return null;
    }

    @Override
    public Transaction listenTransaction() {
        return null;
    }
}
