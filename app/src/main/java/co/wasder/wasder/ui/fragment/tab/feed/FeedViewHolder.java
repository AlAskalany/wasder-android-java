package co.wasder.wasder.ui.fragment.tab.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.Utils;
import co.wasder.wasder.data.model.AbstractFirestoreItem;
import co.wasder.wasder.data.model.FeedModel;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.network.GlideApp;
import co.wasder.wasder.ui.activity.detail.ProfileActivity;
import co.wasder.wasder.ui.fragment.tab.adapter.FirestoreItemsAdapter;

/**
 * Created by Ahmed AlAskalany on 11/12/2017.
 * Navigator
 */
@SuppressWarnings("WeakerAccess")
public class FeedViewHolder extends RecyclerView.ViewHolder {

    private static final String USERS = "users";
    private static final String FOLLOWERS = "followers";
    @BindView(R.id.itemProfileImageView)
    ImageView profileImageView;
    @BindView(R.id.presenceImageView)
    ImageView presenceImageView;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.timeStamp)
    TextView timeStamp;
    @BindView(R.id.expandButton)
    ImageButton expandButton;
    @BindView(R.id.itemImageView)
    ImageView itemImage;
    @BindView(R.id.itemTextView)
    TextView itemText;
    @BindView(R.id.commentImageButton)
    ImageButton commentImageButton;
    @BindView(R.id.shareImageButton)
    ImageButton shareImageButton;
    @BindView(R.id.likeImageButton)
    ImageButton likeImageButton;
    @BindView(R.id.sendImageButton)
    ImageButton sendImageButton;

    FeedViewHolder(@NonNull final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(@NonNull final FeedModel model, final FirestoreItemsAdapter
            .OnFirestoreItemSelected onFirestoreItemSelected) {
        final String userId = model.getUid();
        final CollectionReference users = FirebaseUtil.getUsersCollectionReference(Utils.USERS);
        final DocumentReference userReference = users.document(userId);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                final User user = task.getResult().toObject(User.class);
                setUserName(user, userName);
            }
        });
        String uuid = model.getPhoto();
        if (isProfilePhotoUrlValid(uuid)) {
            setPostImage(uuid, getContext(), itemImage, isViewInvisible(), itemImage);
        }
        final String profilePhotoUrl = model.getProfilePhoto();
        if (profilePhotoUrl != null) {
            setProfilePicture(userId, profilePhotoUrl, getContext());
        }
        getProfileImageView(userId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                startAuthProfileActivity(model, getContext(), itemView);
            }
        });
        final Date date = model.getTimestamp();
        if (date != null) {
            setDate(date, timeStamp);
        }
        setPostText(model, itemText);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                handleItemViewClick(model, onFirestoreItemSelected);
            }
        });
        expandButton.setOnClickListener(new View.OnClickListener() {
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

    private void handleItemViewClick(@NonNull AbstractFirestoreItem model, FirestoreItemsAdapter
            .OnFirestoreItemSelected onFirestoreItemSelected) {
        onFirestoreItemSelected.onFirestoreItemSelected(model, itemView);
    }

    private void handleExpandButton(@NonNull View v, @NonNull final AbstractFirestoreItem model) {
        final String currentUserId = getCurrentUserId();
        final PopupMenu popup = getPopupMenu(v, currentUserId, model, expandButton);
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
        final FirebaseAuth auth = FirebaseUtil.getAuth();
        final FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        return user.getUid();
    }

    @NonNull
    private PopupMenu getPopupMenu(@NonNull View v, String currentUserId, @NonNull
            AbstractFirestoreItem model, ImageButton expandButton) {
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

    private void setPostText(@NonNull AbstractFirestoreItem model, TextView itemText) {
        itemText.setText(model.getFeedText());
    }

    private void setDate(Date date, TextView timeStamp) {
        final String dateString = new SimpleDateFormat().format(date);
        timeStamp.setText(dateString);
    }

    private void startAuthProfileActivity(@NonNull AbstractFirestoreItem model, Context context,
                                          View itemView) {
        if (model.getUid() != null) {
            final Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user-reference", model.getUid());
            itemView.getContext().startActivity(intent);
        }
    }

    private void setPostImage(String uuid, Context context, ImageView itemImage, boolean
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
                .into(getProfileImageView(userId));
    }

    private void handleMenu(@NonNull MenuItem item, @NonNull AbstractFirestoreItem model, String
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

    private void followAuthor(@NonNull AbstractFirestoreItem model, String currentUserId) {
        addCurrentUidToAuthorFollowers(currentUserId, getAuthorFollowersReference(model));
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

    private DatabaseReference getAuthorFollowersReference(@NonNull AbstractFirestoreItem model) {
        return getAuthorUserId(model.getUid()).child(FOLLOWERS);
    }

    private DatabaseReference getUsersDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference(USERS);
    }

    private void deletePost(@NonNull AbstractFirestoreItem model) {
        final FirebaseFirestore firestore = FirebaseUtil.getFirestore();
        firestore.collection(Utils.POSTS)
                .document(model.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull final Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(FeedTabFragment.TAG, "onComplete: ");
                        }
                    }
                });
    }

    ImageView getProfileImageView(@NonNull final String uid) {
        getAuthorUserId(uid).child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String myPresence = dataSnapshot.getValue().toString();
                    handlePresence(myPresence, itemView.getResources()
                            .getDrawable(R.drawable.ic_presence_status_online),
                            getOfflineDrawable(R.drawable.ic_presence_status_offline));
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {

            }
        });
        return profileImageView;
    }

    private Drawable getOfflineDrawable(int ic_presence_status_offline) {
        return itemView.getResources().getDrawable(ic_presence_status_offline);
    }

    private DatabaseReference getAuthorUserId(@NonNull String uid) {
        return getUsersDatabaseReference().child(uid);
    }

    private void handlePresence(String myPresence, Drawable onlineDrawable, Drawable
            offlineDrawable) {
        if (myPresence.equals("true")) {
            setPresence(onlineDrawable, presenceImageView);
        } else if (myPresence.equals("false")) {
            setPresence(offlineDrawable, presenceImageView);
        }
    }

    private void setPresence(Drawable drawable, ImageView presenceImageView) {
        presenceImageView.setImageDrawable(drawable);
    }
}
