package project.skripsi.kateringin.Model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable{
    String name;
    String phoneNumber;
    String BOD;
    String email;
    String gender;
    String profileImageUrl;

    Bitmap profilePlaceHolder;

    public User(String name, String phoneNumber, String BOD, String gender) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.BOD = BOD;
        this.gender = gender;
    }

    public User(String name, String phoneNumber, String BOD, String email, String gender) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.BOD = BOD;
        this.email = email;
        this.gender = gender;
    }

    public User() {
    }


    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBOD() {
        return BOD;
    }

    public void setBOD(String BOD) {
        this.BOD = BOD;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
