package com.avr.examples;

import com.avr.blocklogic.Blockchain;
import com.avr.mockservices.TransactionGenerator;

public class Example2 {
    public static void main(String[] args) {
        Blockchain chain = new Blockchain();
        chain.makeBlock(TransactionGenerator.getNextList());
        chain.makeBlock(TransactionGenerator.getNextList());
        chain.makeBlock(TransactionGenerator.getNextList());
        assert chain.isValidOrder();
        System.out.println(chain.size());
    }
}
