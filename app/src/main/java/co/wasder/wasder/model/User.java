package co.wasder.wasder.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@IgnoreExtraProperties
public class User {

    private String uId;
    private String displayName;
    private String email;
    private String photoUrl;
    private String firstName;
    private String lastName;

    public User() {
    }

    public User(FirebaseUser user, String firstName, String lastName) {
        uId = user.getUid();
        displayName = user.getDisplayName();
        email = user.getEmail();
        photoUrl = String.valueOf(user.getPhotoUrl());
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void addToFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference reference = firestore.collection("users");
        DocumentReference documentReference = reference.document(uId);
        documentReference.set(this);
    }
}
