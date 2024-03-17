package project.skripsi.kateringin.RecyclerviewItem;

import java.io.Serializable;

public class CartItem implements Serializable {

    String foodId;
    String foodName;
    String foodPrice;
    String storeId;
    String storeName;
    Integer quantity;
    String date;
    String time;

    public CartItem(String foodId, String foodName, String foodPrice, String storeId, String storeName, Integer quantity, String date, String time) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.storeId = storeId;
        this.storeName = storeName;
        this.quantity = quantity;
        this.date = date;
        this.time = time;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
