package co.wasder.wasder.data;

import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/** Created by Ahmed AlAskalany on 10/11/2017. Wasder AB */
@Keep
@IgnoreExtraProperties
public class FeedModel extends BaseModel {

    private String uId;
    private @ServerTimestamp Date timestamp;
    private String name;
    private String profilePhoto;
    private String photo;
    private String feedText;

    public FeedModel() {}

    public FeedModel(
            final String uId,
            String name,
            final String profilePhoto,
            final String photo,
            final String feedText) {
        this.uId = uId;
        this.name = name;
        this.profilePhoto = profilePhoto;
        this.photo = photo;
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
        Date myTimestamp = timestamp;
        return myTimestamp;
    }

    public void setTimestamp(final Date timestamp) {
        Date myTimeStampe = timestamp;
        this.timestamp = myTimeStampe;
    }
}
