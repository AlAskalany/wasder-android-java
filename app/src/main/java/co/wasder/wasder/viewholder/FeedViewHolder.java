package co.wasder.wasder.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.wasder.data.base.BaseModel;
import co.wasder.data.model.FeedModel;
import co.wasder.data.model.User;
import co.wasder.wasder.GlideApp;
import co.wasder.wasder.R;
import co.wasder.wasder.activity.ProfileActivity;
import co.wasder.wasder.base.BaseViewHolder;
import co.wasder.wasder.databinding.ItemFeedBinding;
import co.wasder.wasder.listener.OnFirestoreItemSelectedListener;

/**
 * Created by Ahmed AlAskalany on 11/12/2017.
 * Navigator
 */
@SuppressWarnings("WeakerAccess")
public class FeedViewHolder extends BaseViewHolder {

    private static final String USERS = "users";
    private static final String FOLLOWERS = "followers";
    private final ItemFeedBinding binding;

    public FeedViewHolder(@NonNull final ItemFeedBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(@NonNull final FeedModel model, final OnFirestoreItemSelectedListener
            onFirestoreItemSelectedListener) {

        binding.setFeedModel(model);
        binding.executePendingBindings();

        final String userId = model.getUid();
        final CollectionReference users = FirebaseFirestore.getInstance().collection("users");
        final DocumentReference userReference = users.document(userId);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                final User user = task.getResult().toObject(User.class);
                setUserName(user, binding.userName);
            }
        });
        String uuid = model.getPhoto();
        if (isProfilePhotoUrlValid(uuid)) {
            setItemImage(uuid, getContext(), binding.itemImageView, isViewInvisible(), binding
                    .itemImageView);
        }
        final String profilePhotoUrl = model.getProfilePhoto();
        if (profilePhotoUrl != null) {
            setProfilePicture(userId, profilePhotoUrl, getContext());
        }
        binding.itemProfileImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                startAuthProfileActivity(model, getContext(), itemView);
            }
        });
        final Date date = model.getTimestamp();
        if (date != null) {
            setDate(date, binding.timeStamp);
        }
        setPostText(model, binding.itemTextView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                handleItemViewClick(model, onFirestoreItemSelectedListener);
            }
        });
        binding.expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull final View v) {
                handleExpandButton(v, model);
            }
        });
    }

    private Context getContext() {
        return itemView.getContext();
    }

    private boolean isViewInvisible() {
        return itemView.getVisibility() == View.GONE;
    }

    private void handleItemViewClick(@NonNull BaseModel model, OnFirestoreItemSelectedListener
            onFirestoreItemSelectedListener) {
        onFirestoreItemSelectedListener.onFirestoreItemSelected(model, itemView);
    }

    private void handleExpandButton(@NonNull View v, @NonNull final BaseModel model) {
        final String currentUserId = getCurrentUserId();
        final PopupMenu popup = getPopupMenu(v, currentUserId, model, binding.expandButton);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull final MenuItem item) {
                handleMenu(item, model, currentUserId);
                return false;
            }
        });
        popup.show();
    }

    @NonNull
    private String getCurrentUserId() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        return user.getUid();
    }

    @NonNull
    private PopupMenu getPopupMenu(@NonNull View v, String currentUserId, @NonNull BaseModel
            model, ImageButton expandButton) {
        final PopupMenu popup = new PopupMenu(v.getContext(), expandButton);
        popup.inflate(R.menu.menu_item);
        if (!TextUtils.equals(model.getUid(), currentUserId)) {
            popup.getMenu().getItem(1).setVisible(false);
            popup.getMenu().getItem(2).setVisible(true);
        }
        return popup;
    }

    private void setUserName(User user, TextView userName) {
        final String name = user.getDisplayName();
        userName.setText(name);
    }

    private void setPostText(@NonNull BaseModel model, TextView itemText) {
        itemText.setText(model.getFeedText());
    }

    private void setDate(Date date, TextView timeStamp) {
        final String dateString = new SimpleDateFormat().format(date);
        timeStamp.setText(dateString);
    }

    private void startAuthProfileActivity(@NonNull BaseModel model, Context context, View
            itemView) {
        if (model.getUid() != null) {
            final Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user-reference", model.getUid());
            itemView.getContext().startActivity(intent);
        }
    }

    private void setItemImage(String uuid, Context context, ImageView itemImage, boolean
            isViewInvisible, ImageView itemImage1) {
        final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        downloadPostImageIntoView(mImageRef, context, itemImage);
        if (itemImage.getVisibility() == View.GONE) {
            itemImage1.setVisibility(View.VISIBLE);
        }
    }

    private void downloadPostImageIntoView(StorageReference mImageRef, Context context, ImageView
            itemImage) {
        GlideApp.with(context)
                .load(mImageRef)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemImage);
    }

    private void setProfilePicture(String userId, String profilePhotoUrl, Context context) {
        if (isProfilePhotoUrlValid(profilePhotoUrl)) {
            downloadProfilePictureIntoView(userId, profilePhotoUrl, context);
        }
    }

    private boolean isProfilePhotoUrlValid(String profilePhotoUrl) {
        return !TextUtils.isEmpty(profilePhotoUrl);
    }

    private void downloadProfilePictureIntoView(String userId, String profilePhotoUrl, Context
            context) {
        GlideApp.with(context)
                .load(profilePhotoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.itemProfileImageView);
    }

    private void handleMenu(@NonNull MenuItem item, @NonNull BaseModel model, String
            currentUserId) {
        switch (item.getItemId()) {
            case R.id.edit:
                break;
            case R.id.delete:
                deletePost(model);
                break;
            case R.id.follow:
                followAuthor(model, currentUserId);
                break;
        }
    }

    private void followAuthor(@NonNull BaseModel model, String currentUserId) {
        addCurrentUidToAuthorFollowers(currentUserId, FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(model.getUid())
                .child(FOLLOWERS));
    }

    private void addCurrentUidToAuthorFollowers(String currentUserId, DatabaseReference
            followedFollowersRef) {
        followedFollowersRef.updateChildren(getCurrentUidStringObjectMap(currentUserId));
    }

    @NonNull
    private Map<String, Object> getCurrentUidStringObjectMap(String currentUserId) {
        final Map<String, Object> data = new HashMap<>();
        data.put(currentUserId, true);
        return data;
    }

    private void deletePost(@NonNull BaseModel model) {
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts")
                .document(model.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull final Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }

    ImageView getProfileImageView(@NonNull final String uid) {
        FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(uid)
                .child("online")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String myPresence = dataSnapshot.getValue().toString();
                            if (myPresence.equals("true")) {
                                binding.presenceImageView.setImageDrawable(itemView.getResources()
                                        .getDrawable(R.drawable.ic_presence_status_online));
                            } else if (myPresence.equals("false")) {
                                binding.presenceImageView.setImageDrawable(itemView.getResources()
                                        .getDrawable(R.drawable.ic_presence_status_offline));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(final DatabaseError databaseError) {

                    }
                });
        return binding.itemProfileImageView;
    }

}
