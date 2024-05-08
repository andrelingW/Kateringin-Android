package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class OrderItem implements Serializable {
    String orderItemId;
    String menuId;
    int price;
    int quantity;
    String timeRange;
    String date;
    String note;
    String orderItemStatus;
    String orderItemLinkTracker;
    Boolean isReschedule;

    public OrderItem() {
    }

    public OrderItem(String orderItemId, String menuId, int price, int quantity, String timeRange, String date, String note) {
        this.orderItemId = orderItemId;
        this.menuId = menuId;
        this.price = price;
        this.quantity = quantity;
        this.timeRange = timeRange;
        this.date = date;
        this.note = note;
    }

    public OrderItem(String orderItemId, String menuId, int price, int quantity, String timeRange, String date, String note, String orderItemStatus) {
        this.orderItemId = orderItemId;
        this.menuId = menuId;
        this.price = price;
        this.quantity = quantity;
        this.timeRange = timeRange;
        this.date = date;
        this.note = note;
        this.orderItemStatus = orderItemStatus;
    }

    public OrderItem(String orderItemId, String menuId, int price, int quantity, String timeRange, String date, String note, String orderItemStatus, String orderItemLinkTracker, Boolean isReschedule) {
        this.orderItemId = orderItemId;
        this.menuId = menuId;
        this.price = price;
        this.quantity = quantity;
        this.timeRange = timeRange;
        this.date = date;
        this.note = note;
        this.orderItemStatus = orderItemStatus;
        this.orderItemLinkTracker = orderItemLinkTracker;
        this.isReschedule = isReschedule;
    }

    public String getOrderItemLinkTracker() {
        return orderItemLinkTracker;
    }

    public void setOrderItemLinkTracker(String orderItemLinkTracker) {
        this.orderItemLinkTracker = orderItemLinkTracker;
    }

    public Boolean getReschedule() {
        return isReschedule;
    }

    public void setReschedule(Boolean reschedule) {
        isReschedule = reschedule;
    }

    public String getOrderItemStatus() {
        return orderItemStatus;
    }

    public void setOrderItemStatus(String orderItemStatus) {
        this.orderItemStatus = orderItemStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
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
