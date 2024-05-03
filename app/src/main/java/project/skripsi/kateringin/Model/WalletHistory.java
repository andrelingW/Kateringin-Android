package project.skripsi.kateringin.Model;

public class WalletHistory {
    String userId;
    String transactionType;
    int amount;
    String date;

    public WalletHistory(String userId, String transactionType, int amount, String date) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
    }

    public WalletHistory() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
