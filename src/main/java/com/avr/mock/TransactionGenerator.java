package com.avr.mock;

import com.avr.blocklogic.Transaction;

import java.util.Random;

public class TransactionGenerator {

    private static final Random random = new Random();
    private static long id = 0;

    public static Transaction getNext() {
        String sender = KeyGenerator.getPublic();
        String receiver = KeyGenerator.getPublic();
        double amount = random.nextDouble();
        return new Transaction(id++, sender, receiver, amount);
    }
}
