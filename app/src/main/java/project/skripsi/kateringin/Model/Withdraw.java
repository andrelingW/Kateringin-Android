package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class Withdraw implements Serializable {
    String documentId;
    String userBankAccountId;
    String amount;
    String withdrawDate;

    public Withdraw() {
    }

    public Withdraw(String documentId, String userBankAccountId, String amount, String withdrawDate) {
        this.documentId = documentId;
        this.userBankAccountId = userBankAccountId;
        this.amount = amount;
        this.withdrawDate = withdrawDate;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserBankAccountId() {
        return userBankAccountId;
    }

    public void setUserBankAccountId(String userBankAccountId) {
        this.userBankAccountId = userBankAccountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(String withdrawDate) {
        this.withdrawDate = withdrawDate;
    }
}
