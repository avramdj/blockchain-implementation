package com.avr.blocklogic;

public class BlockInterrupt extends Exception {
    Block betterBlock;
    public BlockInterrupt(Block betterBlock) {
        super();
        this.betterBlock = betterBlock;
    }

    public Block getBlock() {
        return betterBlock;
    }
}
