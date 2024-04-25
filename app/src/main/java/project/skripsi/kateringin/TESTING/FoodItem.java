package project.skripsi.kateringin.TESTING;

import android.net.Uri;

import java.io.Serializable;

public class FoodItem implements Serializable {
    String foodId;
    String foodName;
    Uri imageUrl;
    Integer price;
    Integer rate;
    String shopName;
    Uri shopImageUrl;

    public FoodItem(String foodId, String foodName, Uri imageUrl, Integer price, Integer rate, String shopName, Uri shopImageUrl) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.rate = rate;
        this.shopName = shopName;
        this.shopImageUrl = shopImageUrl;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }



    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Uri getShopImageUrl() {
        return shopImageUrl;
    }

    public void setShopImageUrl(Uri shopImageUrl) {
        this.shopImageUrl = shopImageUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}
