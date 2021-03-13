package com.avr.mock;

import com.avr.blocklogic.Block;
import com.avr.blocklogic.Blockchain;
import com.avr.blocklogic.TimeServer;
import com.avr.blocklogic.Transaction;

import java.util.ArrayList;
import java.util.List;

public class Example1 {
    public static void main(String[] args) {

        Blockchain chain = new Blockchain();
        List<Transaction> firstTransactions = new ArrayList<>();
        firstTransactions.add(TransactionGenerator.getNext());
        Block genesis = new Block(firstTransactions, "it's ya boy", TimeServer.time());
        String genesisHash = genesis.mineBlock();
        chain.add(genesis);

        List<Transaction> transactions = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            transactions.add(TransactionGenerator.getNext());
            if(i % 5 == 0){
                Block B = new Block(transactions, chain.getLastHash(), TimeServer.time());
                B.mineBlock();
                chain.add(B);
            }
        }

        assert chain.isValidOrder();

        System.out.println(chain.getLast().getHash());
    }
}
