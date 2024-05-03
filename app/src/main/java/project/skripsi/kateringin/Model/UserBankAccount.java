package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class UserBankAccount implements Serializable {
    String documentId;
    String accountHolderName;
    String bankName;
    String bankNumber;

    public UserBankAccount() {
    }

    public UserBankAccount(String documentId, String accountHolderName, String bankName, String bankNumber) {
        this.documentId = documentId;
        this.accountHolderName = accountHolderName;
        this.bankName = bankName;
        this.bankNumber = bankNumber;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }
}
