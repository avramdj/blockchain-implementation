package com.avr.mockservices;

import com.avr.blocklogic.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class TransactionGenerator {

    private static final Random random = new Random();
    private static final long defaultListSize = 5;
    private static long id = 0;

    public static Transaction getNext() {
        String sender = KeyGenerator.getPublic();
        String receiver = KeyGenerator.getPublic();
        long amount = abs(random.nextLong()+1);
        return new Transaction(id++, sender, receiver, amount);
    }

    public static List<Transaction> getNextList() {
        List<Transaction> transactions = new ArrayList<>();
        for(int i = 0; i < defaultListSize; i++){
            transactions.add(getNext());
        }
        return transactions;
    }

    public static List<Transaction> getNextList(long size) {
        List<Transaction> transactions = new ArrayList<>();
        for(int i = 0; i < size; i++){
            transactions.add(getNext());
        }
        return transactions;
    }

}
