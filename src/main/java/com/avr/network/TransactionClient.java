package com.avr.network;

import com.avr.blocklogic.Transaction;

import java.util.List;

public interface TransactionClient {
    public void broadcastTransaction(Transaction tx);
    public List<Transaction> getTransactions();
    public void confirmTransactions(List<Transaction> txs);
    public long transactionsOnWait();
}
