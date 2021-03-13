package com.avr.network;

import com.avr.blocklogic.Block;
import com.avr.blocklogic.Transaction;

public interface NodeNetwork {
    public void broadcastBlock(Block b);
    public void broadcastTransaction(Transaction t);
    public void requestBlock(String hash);
    public Block listenBlock();
    public Transaction listenTransaction();
}
