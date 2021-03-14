package com.avr.blocklogic;

import com.avr.network.BlockClient;

import java.util.Optional;

public class Signal {
    private BlockClient blockClient;

    public Signal(BlockClient bc) {
        blockClient = bc;
    }

    public Optional<Block> poll() {
        return Optional.ofNullable(blockClient.getBlock());
    }
}
