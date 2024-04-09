package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class Cart implements Serializable {
    String cartItemId;
    String menuId;
    String storeId;
    String userId;
    String date;
    String timeRange;
    int quantity;
    int price;
    boolean processed;

    public Cart(String cartItemId, String menuId, String storeId, String userId, String date, String timeRange, int quantity, int price, boolean processed) {
        this.cartItemId = cartItemId;
        this.menuId = menuId;
        this.storeId = storeId;
        this.userId = userId;
        this.date = date;
        this.timeRange = timeRange;
        this.quantity = quantity;
        this.price = price;
        this.processed = processed;
    }


    public Cart() {
    }

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartItemId='" + cartItemId + '\'' +
                ", menuId='" + menuId + '\'' +
                ", storeId='" + storeId + '\'' +
                ", userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", timeRange='" + timeRange + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", processed=" + processed +
                '}';
    }
}
