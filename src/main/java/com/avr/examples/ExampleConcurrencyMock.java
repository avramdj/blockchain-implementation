package com.avr.examples;

import com.avr.blocklogic.*;
import com.avr.mockservices.TransactionGenerator;
import com.avr.network.BlockClient;
import com.avr.network.CentralBlockClient;
import com.avr.network.CentralTransactionClient;
import com.avr.network.TransactionClient;
import com.avr.util.AsyncLogger;
import com.avr.util.ConsoleColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static java.lang.Math.abs;

public class ExampleConcurrencyMock {

    private static final AsyncLogger logger = new AsyncLogger(Block.class.getSimpleName());
    private static Level loggingLevel = Level.INFO;
    private static Level transactionLogging = Level.INFO;

    private static class MinerNode extends Thread {
        private int id;
        private static int minerCount = 0;

        public MinerNode() {
            id = minerCount++;
        }

        public MinerNode(int transactionCount) { }

        @Override
        public void run() {
            logger.log(loggingLevel, String.format("Miner %d started%n", id), ConsoleColor.CYAN);
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
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        return;
                    }
                    newTx = txClient.getTransactions();
                    if(newTx.isEmpty()) {
                        break;
                    }
                }
                Block b = null;
                try {
                    b = chain.makeBlock(newTx, stopCondition);
                    created++;
                    txClient.confirmTransactions(newTx);
                    logger.log(loggingLevel,
                                    "Miner " + id +
                                    " is broadcasting block " + b.getHash() +
                                    " nonce: " + b.getNonce() +
                                    " transactions left: " + txClient.transactionsOnWait() +
                                    "\n",
                            ConsoleColor.GREEN);
                    blClient.broadcastBlock(b);
                } catch (StoppedException e) {
                    b = stopCondition.pop();
                    logger.log(loggingLevel,
                                    "Miner " + id +
                                    " accepted block " + b.getHash() +
                                    "\n",
                            ConsoleColor.YELLOW);
                }
            }
            CentralTransactionClient.disconnect(txClient);
            CentralBlockClient.disconnect(blClient);
        }
    }

    private static class UserNode extends Thread {
        private int transactionCount = 20;
        private int id;
        private static int userCount = 0;
        private Random random;

        public UserNode() {
            id = userCount++;
            random = new Random();
        }

        public UserNode(int transactionCount) {
            this.transactionCount = transactionCount;
        }

        @Override
        public void run() {
            logger.log(loggingLevel, String.format("User %d started%n", id), ConsoleColor.CYAN);
            TransactionClient txClient = CentralTransactionClient.createConnection();
            for(int i = 0; i < transactionCount; i++){
                Transaction tx =  TransactionGenerator.getNext();
                txClient.broadcastTransaction(tx);
                logger.log(transactionLogging,
                        String.format("User %2d broadcasted transaction %d\n", id, i),
                        ConsoleColor.RED);
                try {
                    TimeUnit.MILLISECONDS.sleep(abs(random.nextInt())%2000 + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            logger.log(
                    loggingLevel,
                    String.format(
                            "User %d exited, %d remaining%n",
                            id, CentralTransactionClient.userCount()),
                    ConsoleColor.CYAN);
            CentralTransactionClient.disconnect(txClient);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //transactionLogging = Level.FINE;
        int numUsers = 15;
        int numMiners = 5;
        List<Thread> threads = new ArrayList<>();
        List<UserNode> users = new ArrayList<>();
        List<MinerNode> miners = new ArrayList<>();
        for(int i = 0; i < numUsers; i++){
            UserNode un = new UserNode();
            Thread thr = new Thread(un);
            thr.start();
            threads.add(thr);
        }
        TimeUnit.SECONDS.sleep(3);
        for(int i = 0; i < numMiners; i++){
            MinerNode mn = new MinerNode();
            Thread thr = new Thread(mn);
            thr.start();
        }
    }
}
