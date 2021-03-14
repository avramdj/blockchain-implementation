package com.avr.examples;

import com.avr.blocklogic.*;
import com.avr.mockservices.TransactionGenerator;
import com.avr.network.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExampleConcurrency {

    private static final Logger logger = Logger.getLogger(Block.class.getName());

    private static class MinerNode extends Thread {
        private int id;
        private static int minerCount = 0;

        public MinerNode() {
            id = minerCount++;
        }

        public MinerNode(int transactionCount) { }

        @Override
        public void run() {
            System.out.printf("Miner %d started%n", id);
            Blockchain chain = new Blockchain();
            TransactionClient txClient = CentralTransactionClient.createConnection();
            BlockClient blClient = CentralBlockClient.createConnection();
            int created = 0;
            List<Transaction> newTx = new ArrayList<>();
            //while(CentralTransactionClient.hasUsers() || newTx.isEmpty()){
            Signal stopCondition = new Signal(blClient);
            boolean run = true;
            while(run){
                //System.out.println(CentralTransactionClient.userCount());
                //System.out.println(newTx.isEmpty());
                newTx = txClient.getTransactions();
                if(newTx.isEmpty()) {
                    continue;
                }
                Block b = chain.makeBlock(newTx, stopCondition);
                created++;
                txClient.confirmTransactions(newTx);
                blClient.broadcastBlock(b);
                System.out.print(
                        "Miner " + id + " created block \n" +
                        "attempt: " + b.getNonce() + "\n" +
                        "hash: " + b.getHash() + "\n" +
                        "transactions left: " + txClient.transactionsOnWait() +
                        "\n");
            }
            CentralTransactionClient.disconnect(txClient);
            CentralBlockClient.disconnect(blClient);
        }
    }

    private static class UserNode extends Thread {
        private int transactionCount = 100;
        private int id;
        private static int userCount = 0;

        public UserNode() {
            id = userCount++;
        }

        public UserNode(int transactionCount) {
            this.transactionCount = transactionCount;
        }

        @Override
        public void run() {
            System.out.printf("User %d started%n", id);
            TransactionClient txClient = CentralTransactionClient.createConnection();
            for(int i = 0; i < transactionCount; i++){
                Transaction tx =  TransactionGenerator.getNext();
                txClient.broadcastTransaction(tx);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out.printf("User %d exited, %d remaining%n", id, CentralTransactionClient.userCount());
            CentralTransactionClient.disconnect(txClient);
        }
    }

    public static void main(String[] args) {
        int numUsers = 20;
        int numMiners = 10;
        List<Thread> threads = new ArrayList<>();
        List<UserNode> users = new ArrayList<>();
        List<MinerNode> miners = new ArrayList<>();
        for(int i = 0; i < numUsers; i++){
            UserNode un = new UserNode();
            Thread thr = new Thread(un);
            thr.start();
            threads.add(thr);
        }
        for(int i = 0; i < numMiners; i++){
            MinerNode mn = new MinerNode();
            Thread thr = new Thread(mn);
            thr.start();
        }
    }
}
