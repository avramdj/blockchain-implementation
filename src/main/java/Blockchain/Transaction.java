package Blockchain;

public class Transaction {
    private int id;
    private String sender;
    private String receiver;
    private double amount;

    public Transaction(int id, String sender, String receiver, double amount) {
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
