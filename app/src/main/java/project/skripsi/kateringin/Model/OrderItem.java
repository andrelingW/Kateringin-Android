package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class OrderItem implements Serializable {
    String cartItemId;
    String menuId;
    int price;
    int quantity;
    String timeRange;
    String date;
    String note;

    public OrderItem() {
    }

    public OrderItem(String cartItemId, String menuId, int price, int quantity, String timeRange, String date, String note) {
        this.cartItemId = cartItemId;
        this.menuId = menuId;
        this.price = price;
        this.quantity = quantity;
        this.timeRange = timeRange;
        this.date = date;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
