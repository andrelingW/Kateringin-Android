package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class OrderItem implements Serializable {
    String cartItemId;
    String menuId;
    int price;
    int quantity;
    String timeRange;
    String date;

    public OrderItem(String cartItemId, String menuId, int price, int quantity, String timeRange, String date) {
        this.cartItemId = cartItemId;
        this.menuId = menuId;
        this.price = price;
        this.quantity = quantity;
        this.timeRange = timeRange;
        this.date = date;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
