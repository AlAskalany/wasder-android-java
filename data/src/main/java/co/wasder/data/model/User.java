package co.wasder.data.model;

import android.net.Uri;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
@IgnoreExtraProperties
public class User {

    private String uId;
    @Nullable
    private String displayName;
    @Nullable
    private String email;
    private String photoUrl;
    private String firstName;
    private String lastName;

    public User() {
    }

    public User(@NonNull final FirebaseUser user, final String firstName, final String lastName) {
        uId = user.getUid();
        displayName = user.getDisplayName();
        email = user.getEmail();
        final Uri uri = user.getPhotoUrl();
        if (uri != null) {
            photoUrl = uri.toString();
        }
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUid() {
        return uId;
    }

    public void setUid(final String uId) {
        this.uId = uId;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(final String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Exclude
    public void addToFirestore() {
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final CollectionReference reference = firestore.collection("users");
        final DocumentReference documentReference = reference.document(uId);
        final User myUser = this;
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                if (task.getResult() != null) {
                    if (!task.getResult().exists()) {
                        documentReference.set(myUser);
                    }
                }
            }
        });
    }
}
