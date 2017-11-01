package co.wasder.wasder.model;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

public class Rating {

    private String userId;
    private String userName;
    private double rating;
    private String text;
    private @ServerTimestamp
    Date timestamp;

    @SuppressWarnings("unused")
    public Rating() {
    }

    public Rating(FirebaseUser user, double rating, String text) {
        this.userId = user.getUid();
        this.userName = user.getDisplayName();
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user.getEmail();
        }

        this.rating = rating;
        this.text = text;
    }

    @SuppressWarnings("unused")
    public String getUserId() {
        return userId;
    }

    @SuppressWarnings("unused")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    @SuppressWarnings("unused")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getRating() {
        return rating;
    }

    @SuppressWarnings("unused")
    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    @SuppressWarnings("unused")
    public void setText(String text) {
        this.text = text;
    }

    @SuppressWarnings("unused")
    public Date getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("unused")
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
