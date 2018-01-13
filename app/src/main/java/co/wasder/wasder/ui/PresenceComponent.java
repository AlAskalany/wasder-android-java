package co.wasder.wasder.ui;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class PresenceComponent {

    public PresenceComponent() {
    }

    void setOnlineStatus(String status, FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("online")
                    .setValue(status);
        }
    }
}