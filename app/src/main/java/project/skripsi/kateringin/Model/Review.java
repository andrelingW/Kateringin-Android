package project.skripsi.kateringin.Model;

import java.io.Serializable;

public class Review implements Serializable {
    String reviewId;
    String menuId;
    String userId;
    Double rate;
    String comment;

    public Review() {
    }

    public Review(String reviewId, String menuId, String userId, Double rate, String comment) {
        this.reviewId = reviewId;
        this.menuId = menuId;
        this.userId = userId;
        this.rate = rate;
        this.comment = comment;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
