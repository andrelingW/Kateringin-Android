package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class Wallet implements Serializable {
    String userId;
    Double balance;

    public Wallet() {
    }

    public Wallet(String userId, Double balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
