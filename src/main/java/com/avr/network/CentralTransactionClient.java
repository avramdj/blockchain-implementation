package com.avr.network;

import com.avr.blocklogic.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CentralTransactionClient implements TransactionClient {

    private List<Transaction> txQueue;
    private final static int maxTransfer = 100000;
    private static Set<CentralTransactionClient> clients = new HashSet<>();
    private static long txCount = 0;

    public static CentralTransactionClient createConnection(){
        CentralTransactionClient ctc = new CentralTransactionClient();
        ctc.txQueue = new ArrayList<>();
        clients.add(ctc);
        return ctc;
    }

    private CentralTransactionClient() { }

    public static boolean hasUsers() {
        return clients.size() > 0;
    }

    public static int userCount() {
        return clients.size();
    }

    public static void disconnect(TransactionClient txClient) {
        clients.remove(txClient);
    }

    @Override
    public void broadcastTransaction(Transaction tx) {
        serverBroadcastTransaction(tx, this);
    }

    private static synchronized void serverBroadcastTransaction(Transaction tx, TransactionClient origin){
        for(CentralTransactionClient tc : clients){
            if(tc != origin){
                tc.txQueue.add(tx);
            }
        }
    }

    @Override
    public synchronized List<Transaction> getTransactions() {
        List<Transaction> txList = new ArrayList<>();
        for(int i = 0; i < maxTransfer; i++){
            try{
                txList.add(txQueue.get(i));
            } catch (IndexOutOfBoundsException e){
                break;
            }
        }
        return txList;
    }

    public void confirmTransactions(List<Transaction> txs){
        for(Transaction tx : txs){
            txQueue.remove(tx);
        }
    }

    @Override
    public long transactionsOnWait() {
        return txQueue.size();
    }
}
