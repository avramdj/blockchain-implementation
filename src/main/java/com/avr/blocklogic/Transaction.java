package com.avr.blocklogic;

public class Transaction {
    private long id;
    private String sender;
    private String receiver;
    private long amount;

    public Transaction(long id, String sender, String receiver, long amount) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", amount=" + amount +
                '}';
    }
}
