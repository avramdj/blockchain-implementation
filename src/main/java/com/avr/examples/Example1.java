package com.avr.examples;

import com.avr.blocklogic.Block;
import com.avr.blocklogic.Blockchain;
import com.avr.blocklogic.Transaction;
import com.avr.mockservices.TransactionGenerator;

import java.util.ArrayList;
import java.util.List;

public class Example1 {
    public static void main(String[] args) {

        Blockchain chain = new Blockchain();
        List<Transaction> firstTransactions = new ArrayList<>();
        firstTransactions.add(TransactionGenerator.getNext());
        Block genesis = new Block(1,firstTransactions, "it's ya boy", 3);
        String genesisHash = genesis.mineBlock();
        chain.add(genesis);

        List<Transaction> transactions = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            transactions.add(TransactionGenerator.getNext());
            if(i % 5 == 0){
                Block B = new Block(1,transactions, chain.getLastHash(), 3);
                B.mineBlock();
                chain.add(B);
            }
        }

        assert chain.isValidOrder();

        System.out.println(chain.getLast().getHash());
    }
}
