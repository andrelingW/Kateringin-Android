package project.skripsi.kateringin.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    String orderId;
    String storeId;
    String userId;
    String receiverAddress;
    String receiverName;
    String receiverPhone;
    String receiverEmail;
    ArrayList<OrderItem> orderItem;

    public Order() {
    }

    public Order(String orderId, String storeId, String userId, String receiverAddress, String receiverName, String receiverPhone, String receiverEmail, ArrayList<OrderItem> orderItem) {
        this.orderId = orderId;
        this.storeId = storeId;
        this.userId = userId;
        this.receiverAddress = receiverAddress;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverEmail = receiverEmail;
        this.orderItem = orderItem;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public ArrayList<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(ArrayList<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", userId='" + userId + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", receiverEmail='" + receiverEmail + '\'' +
                ", orderItem=" + orderItem +
                '}';
    }
}
