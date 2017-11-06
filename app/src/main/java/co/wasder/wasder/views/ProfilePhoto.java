package co.wasder.wasder.views;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
public class ProfilePhoto extends FrameLayout {

    ImageView profileImageView;
    ImageView presenceImageView;

    public ProfilePhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_profile_photo, this, true);
        profileImageView = findViewById(R.id.itemProfileImageView);
        presenceImageView = findViewById(R.id.presenceImageView);
    }

    public void setPresenceOffline() {
        presenceImageView.setImageDrawable(getResources().getDrawable(R.drawable
                .ic_presence_status_offline));
    }

    public void setPresenceOnline() {
        presenceImageView.setImageDrawable(getResources().getDrawable(R.drawable
                .ic_presence_status_online));
    }

    public ImageView getProfileImageView(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = reference.child("users");
        DatabaseReference myUser = users.child(uid);
        myUser.child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Object myPresence = dataSnapshot.getValue().toString();
                    if (myPresence == "true") {
                        setPresenceOnline();
                    } else if (myPresence == "false"){
                        setPresenceOffline();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return profileImageView;
    }
}
