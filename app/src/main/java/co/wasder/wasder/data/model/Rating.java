package co.wasder.wasder.data.model;

import android.support.annotation.Keep;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */
@Keep
public class Rating {

    private String uId;
    private String userName;
    private double rating;
    private String text;
    private @ServerTimestamp
    Date timestamp;

    @SuppressWarnings("unused")
    public Rating() {
    }

    public Rating(final FirebaseUser user, final double rating, final String text) {
        this.uId = user.getUid();
        this.userName = user.getDisplayName();
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user.getEmail();
        }

        this.rating = rating;
        this.text = text;
    }

    @SuppressWarnings("unused")
    public String getUid() {
        return uId;
    }

    @SuppressWarnings("unused")
    public void setUid(final String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    @SuppressWarnings("unused")
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public double getRating() {
        return rating;
    }

    @SuppressWarnings("unused")
    public void setRating(final double rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    @SuppressWarnings("unused")
    public void setText(final String text) {
        this.text = text;
    }

    @SuppressWarnings("unused")
    public Date getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("unused")
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
}
