package project.skripsi.kateringin.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class TransactionResponse implements Serializable {
    public String status_code;
    public String transaction_id;
    public String gross_amount;
    public String currency;
    public String order_id;
    public String payment_type;
    public String signature_key;
    public String transaction_status;
    public String fraud_status;
    public String status_message;
    public String merchant_id;
    public ArrayList<VaNumber> va_numbers;
    public ArrayList<Object> payment_amounts;
    public String transaction_time;
    public String settlement_time;
    public String expiry_time;

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getGross_amount() {
        return gross_amount;
    }

    public void setGross_amount(String gross_amount) {
        this.gross_amount = gross_amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getSignature_key() {
        return signature_key;
    }

    public void setSignature_key(String signature_key) {
        this.signature_key = signature_key;
    }

    public String getTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(String transaction_status) {
        this.transaction_status = transaction_status;
    }

    public String getFraud_status() {
        return fraud_status;
    }

    public void setFraud_status(String fraud_status) {
        this.fraud_status = fraud_status;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public ArrayList<VaNumber> getVa_numbers() {
        return va_numbers;
    }

    public void setVa_numbers(ArrayList<VaNumber> va_numbers) {
        this.va_numbers = va_numbers;
    }

    public ArrayList<Object> getPayment_amounts() {
        return payment_amounts;
    }

    public void setPayment_amounts(ArrayList<Object> payment_amounts) {
        this.payment_amounts = payment_amounts;
    }

    public String getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(String transaction_time) {
        this.transaction_time = transaction_time;
    }

    public String getSettlement_time() {
        return settlement_time;
    }

    public void setSettlement_time(String settlement_time) {
        this.settlement_time = settlement_time;
    }

    public String getExpiry_time() {
        return expiry_time;
    }

    public void setExpiry_time(String expiry_time) {
        this.expiry_time = expiry_time;
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "status_code='" + status_code + '\'' +
                ", transaction_id='" + transaction_id + '\'' +
                ", gross_amount='" + gross_amount + '\'' +
                ", currency='" + currency + '\'' +
                ", order_id='" + order_id + '\'' +
                ", payment_type='" + payment_type + '\'' +
                ", signature_key='" + signature_key + '\'' +
                ", transaction_status='" + transaction_status + '\'' +
                ", fraud_status='" + fraud_status + '\'' +
                ", status_message='" + status_message + '\'' +
                ", merchant_id='" + merchant_id + '\'' +
                ", va_numbers=" + va_numbers +
                ", payment_amounts=" + payment_amounts +
                ", transaction_time='" + transaction_time + '\'' +
                ", settlement_time='" + settlement_time + '\'' +
                ", expiry_time='" + expiry_time + '\'' +
                '}';
    }
}

