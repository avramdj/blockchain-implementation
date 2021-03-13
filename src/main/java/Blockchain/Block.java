package Blockchain;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Block {
    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;
    private int difficulty = 2;

    private static final Logger logger = Logger.getLogger(Block.class.getName());

    public Block(Collection<Transaction> dataCollection, String previousHash, long timeStamp) {
        StringBuilder data = new StringBuilder();
        for(Transaction t : dataCollection){
            data.append(t.toString());
        }
        this.data = data.toString();
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = computeHash();
    }

    private String computeHash() {
        String dataToHash = previousHash
                + Long.toString(timeStamp)
                + Integer.toString(nonce)
                + data;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
            assert bytes != null;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public String mineBlock() {
        String prefixString = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(prefixString)) {
            nonce++;
            hash = computeHash();
            logger.log(Level.INFO, "Mining nonce: " + nonce + " hash: " + hash);
        }
        return hash;
    }

    public static void main(String[] args) {
        List<Transaction> firstTransaction = new ArrayList<>();

        String sender = KeyGeneratorService.getKey().getPrivate().toString();
        String receiver = KeyGeneratorService.getKey().getPrivate().toString();

        firstTransaction.add(new Transaction(0,sender,receiver, 100));

        Block genesis = new Block(firstTransaction, "it's ya boy", TimeFetcher.time());
        String genesisHash = genesis.mineBlock();

        List<Transaction> transactions = new ArrayList<>();
        for(int i = 1; i < 5; i++){
            String s = KeyGeneratorService.getKey().getPrivate().toString();
            String r = KeyGeneratorService.getKey().getPrivate().toString();
            Transaction T = new Transaction(i, s, r, 100+i*10);
            transactions.add(T);
        }

        Block B = new Block(transactions, genesisHash, TimeFetcher.time());
        String hash = B.mineBlock();
        System.out.println(hash);
    }
}