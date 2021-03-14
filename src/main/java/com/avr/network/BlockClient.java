package com.avr.network;

import com.avr.blocklogic.Block;

public interface BlockClient {
    public void broadcastBlock(Block blk);
    public Block getBlock();
}
