package project.skripsi.kateringin.RecyclerviewItem;

import java.io.Serializable;

public class OrderItem implements Serializable {
    String orderId;
    String storeId;
    String orderStatus;

    public OrderItem(String orderId, String storeId, String orderStatus) {
        this.orderId = orderId;
        this.storeId = storeId;
        this.orderStatus = orderStatus;
    }

    public OrderItem() {
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

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
