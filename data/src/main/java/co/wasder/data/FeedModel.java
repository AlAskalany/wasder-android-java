package co.wasder.data;

import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */
@Keep
@IgnoreExtraProperties
public class FeedModel extends AbstractFirestoreItem {

    @SuppressWarnings("unused")
    private static final String FIELD_UID = "uId";
    private static final String FIELD_CITY = "city";
    @SuppressWarnings("unused")
    private static final String FIELD_CATEGORY = "category";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_POPULARITY = "numRatings";
    private static final String FIELD_AVG_RATING = "avgRating";

    private String uId;
    private @ServerTimestamp
    Date timestamp;
    private String name;
    private String profilePhoto;
    private String photo;
    private int numRatings;
    private double avgRating;
    private String feedText;

    public FeedModel() {
    }

    public FeedModel(final String uId, final String profilePhoto, final String photo, final int numRatings, final double
            avgRating, final String
            feedText) {
        this.uId = uId;
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.photo = photo;
        this.numRatings = numRatings;
        this.avgRating = avgRating;
        this.feedText = feedText;
    }

    public String getUid() {
        return uId;
    }

    public void setUid(final String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public String getMessage() {
        return null;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(final String photo) {
        this.photo = photo;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(final int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(final double avgRating) {
        this.avgRating = avgRating;
    }

    public String getFeedText() {
        return feedText;
    }

    public void setFeedText(final String feedText) {
        this.feedText = feedText;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(final String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
}
