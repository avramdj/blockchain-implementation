package com.avr.blocklogic;

import com.avr.network.BlockClient;

public class Signal {
    private BlockClient blockClient;

    public Signal(BlockClient bc) {
        blockClient = bc;
    }

    public Block poll() {
        return blockClient.getBlock();
    }

    public Block pop() {
        return blockClient.pop();
    }
}
