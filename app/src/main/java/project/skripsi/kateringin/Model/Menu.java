package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class Menu implements Serializable {
    String menuId;
    String storeId;
    String menuName;
    String menuDescription;
    String menuPhotoUrl;
    int menuCalorie;
    int menuPrice;
    Double menuRating;
    Boolean isDiet;
    Boolean isNoodle;
    Boolean isPork;
    Boolean isRice;
    Boolean isSoup;
    Boolean isVegan;

    public Menu(String menuId, String storeId, String menuName, String menuDescription, String menuPhotoUrl, int menuCalorie, int menuPrice, Double menuRating, Boolean isDiet, Boolean isNoodle, Boolean isPork, Boolean isRice, Boolean isSoup, Boolean isVegan) {
        this.menuId = menuId;
        this.storeId = storeId;
        this.menuName = menuName;
        this.menuDescription = menuDescription;
        this.menuPhotoUrl = menuPhotoUrl;
        this.menuCalorie = menuCalorie;
        this.menuPrice = menuPrice;
        this.menuRating = menuRating;
        this.isDiet = isDiet;
        this.isNoodle = isNoodle;
        this.isPork = isPork;
        this.isRice = isRice;
        this.isSoup = isSoup;
        this.isVegan = isVegan;
    }

    public Menu() {
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

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuDescription() {
        return menuDescription;
    }

    public void setMenuDescription(String menuDescription) {
        this.menuDescription = menuDescription;
    }

    public String getMenuPhotoUrl() {
        return menuPhotoUrl;
    }

    public void setMenuPhotoUrl(String menuPhotoUrl) {
        this.menuPhotoUrl = menuPhotoUrl;
    }

    public int getMenuCalorie() {
        return menuCalorie;
    }

    public void setMenuCalorie(int menuCalorie) {
        this.menuCalorie = menuCalorie;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }

    public Double getMenuRating() {
        return menuRating;
    }

    public void setMenuRating(Double menuRating) {
        this.menuRating = menuRating;
    }

    public Boolean getDiet() {
        return isDiet;
    }

    public void setDiet(Boolean diet) {
        isDiet = diet;
    }

    public Boolean getNoodle() {
        return isNoodle;
    }

    public void setNoodle(Boolean noodle) {
        isNoodle = noodle;
    }

    public Boolean getPork() {
        return isPork;
    }

    public void setPork(Boolean pork) {
        isPork = pork;
    }

    public Boolean getRice() {
        return isRice;
    }

    public void setRice(Boolean rice) {
        isRice = rice;
    }

    public Boolean getSoup() {
        return isSoup;
    }

    public void setSoup(Boolean soup) {
        isSoup = soup;
    }

    public Boolean getVegan() {
        return isVegan;
    }

    public void setVegan(Boolean vegan) {
        isVegan = vegan;
    }
}
