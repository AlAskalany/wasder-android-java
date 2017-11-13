package co.wasder.wasder.arch;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ahmed AlAskalany on 11/7/2017.
 * Navigator
 */
@IgnoreExtraProperties
class WasderUser implements UserInfo {

    private String uId;
    private String providerId;
    private String email;
    private String phoneNumber;
    private String displayName;
    private Uri photoUrl;
    private boolean isEmailVerified;
    private boolean isAnonymous;

    public WasderUser() {
        // Mandatory empty constructor
    }

    WasderUser(final FirebaseUser firebaseUser) {
        uId = firebaseUser.getUid();
        providerId = firebaseUser.getProviderId();
        email = firebaseUser.getEmail();
        phoneNumber = firebaseUser.getPhoneNumber();
        displayName = firebaseUser.getDisplayName();
        photoUrl = firebaseUser.getPhotoUrl();
        isEmailVerified = firebaseUser.isEmailVerified();
        isAnonymous = firebaseUser.isAnonymous();
    }

    @Override
    public String getUid() {
        return uId;
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Nullable
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Nullable
    @Override
    public Uri getPhotoUrl() {
        return photoUrl;
    }

    @Nullable
    @Override
    public String getEmail() {
        return email;
    }

    @Nullable
    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }
}