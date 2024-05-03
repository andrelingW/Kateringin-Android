package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class Store implements Serializable {
    String storeId;
    String ownerId;
    Double balance;
    String storeName;
    String storeDesc;
    String storePhone;
    String storeUrlPhoto;
    String storeSubDistrict;

    public Store() {
    }

    public Store(String storeId, String ownerId, Double balance, String storeName, String storeDesc, String storePhone, String storeUrlPhoto, String storeSubDistrict) {
        this.storeId = storeId;
        this.ownerId = ownerId;
        this.balance = balance;
        this.storeName = storeName;
        this.storeDesc = storeDesc;
        this.storePhone = storePhone;
        this.storeUrlPhoto = storeUrlPhoto;
        this.storeSubDistrict = storeSubDistrict;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreDesc() {
        return storeDesc;
    }

    public void setStoreDesc(String storeDesc) {
        this.storeDesc = storeDesc;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getStoreUrlPhoto() {
        return storeUrlPhoto;
    }

    public void setStoreUrlPhoto(String storeUrlPhoto) {
        this.storeUrlPhoto = storeUrlPhoto;
    }

    public String getStoreSubDistrict() {
        return storeSubDistrict;
    }

    public void setStoreSubDistrict(String storeSubDistrict) {
        this.storeSubDistrict = storeSubDistrict;
    }
}
