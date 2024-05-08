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
    String orderStatus;
    ArrayList<OrderItem> orderItems;

    public Order() {
    }

    public Order(String orderId, String storeId, String userId, String receiverAddress, String receiverName, String receiverPhone, String orderStatus, ArrayList<OrderItem> orderItems) {
        this.orderId = orderId;
        this.storeId = storeId;
        this.userId = userId;
        this.receiverAddress = receiverAddress;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
