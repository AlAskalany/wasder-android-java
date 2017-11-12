package co.wasder.wasder.ui.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.Util.FirebaseUtil;
import co.wasder.wasder.Utils;
import co.wasder.wasder.data.model.FeedModel;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.network.GlideApp;
import co.wasder.wasder.ui.ProfileActivity;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class FirestoreItemAdapter extends FirestoreRecyclerAdapter<FeedModel,
        FirestoreItemAdapter.FirestoreItemHolder> implements FirestoreItemsAdapter {

    public static final String TAG = "FirestoreItemAdapter";
    public OnFirestoreItemSelected mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions
     */
    public FirestoreItemAdapter(@NonNull final FirestoreRecyclerOptions<FeedModel> options, final
    OnFirestoreItemSelected listener) {
        //noinspection unchecked
        super(options);
        mListener = listener;
    }

    @SuppressWarnings("unused")
    public static FirestoreItemsAdapter newInstance(@NonNull final LifecycleOwner lifecycleOwner,
                                                    final Query query, final
                                                        OnFirestoreItemSelected listener) {
        return Adapters.PostAdapter(lifecycleOwner, query, listener);
    }

    @SuppressWarnings("unused")
    public static FirestoreItemsAdapter newInstance(final Query query, final
    OnFirestoreItemSelected listener) {
        final FirestoreRecyclerOptions<FeedModel> options = new FirestoreRecyclerOptions
                .Builder<FeedModel>()
                .setQuery(query, FeedModel.class)
                .build();
        return new FirestoreItemAdapter(options, listener);
    }

    @NonNull
    private static DocumentReference getAuthorDocumentReference(@NonNull final String userId) {
        final CollectionReference users = FirebaseUtil.getUsersCollectionReference(Utils.USERS);
        return users.document(userId);
    }

    @Override
    public void onBindViewHolder(@NonNull final FirestoreItemHolder holder, final int position,
                                 final FeedModel model) {
        holder.bind(getSnapshots().getSnapshot(position), mListener);
    }

    @NonNull
    @Override
    public FirestoreItemHolder onCreateViewHolder(@NonNull final ViewGroup group, final int
            viewType) {
        final View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_firestore_item, group, false);
        return new FirestoreItemHolder(view);
    }

    /**
     * Created by Ahmed AlAskalany on 10/13/2017.
     * Wasder AB
     */
    @Keep
    public static class FirestoreItemHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "FirestoreItemHolder";
        public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.US);


        ImageView profileImageView;
        ImageView presenceImageView;
        TextView userName;
        TextView timeStamp;
        ImageButton expandButton;
        TextView itemText;
        ImageView itemImage;
        ImageButton commentImageButton;
        ImageButton shareImageButton;
        ImageButton likeImageButton;
        ImageButton sendImageButton;
        String tag;

        public FirestoreItemHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            profileImageView = itemView.findViewById(R.id.itemProfileImageView);
            presenceImageView = itemView.findViewById(R.id.presenceImageView);
            userName = itemView.findViewById(R.id.userName);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            expandButton = itemView.findViewById(R.id.expandButton);
            itemText = itemView.findViewById(R.id.itemTextView);
            itemImage = itemView.findViewById(R.id.itemImageView);
            commentImageButton = itemView.findViewById(R.id.commentImageButton);
            shareImageButton = itemView.findViewById(R.id.shareImageButton);
            likeImageButton = itemView.findViewById(R.id.likeImageButton);
            sendImageButton = itemView.findViewById(R.id.sendImageButton);
        }

        public void bind(@NonNull final DocumentSnapshot snapshot, @NonNull final
        OnFirestoreItemSelected onFirestoreItemSelected) {
            final FeedModel feedModel = snapshot.toObject(FeedModel.class);
            final String userId = feedModel.getUid();
            final DocumentReference userReference = getAuthorDocumentReference(userId);
            final Task<DocumentSnapshot> getUserTask = userReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                        @Override
                        public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                            final User user = task.getResult().toObject(User.class);
                            final String name = user.getDisplayName();
                            userName.setText(name);
                        }
                    });

            // Load image
            String uuid = null;
            uuid = feedModel.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                GlideApp.with(itemView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(itemImage);
                if (itemImage.getVisibility() == View.GONE) {
                    itemImage.setVisibility(View.VISIBLE);
                }
            }
            final String profilePhotoUrl = feedModel.getProfilePhoto();
            if (profilePhotoUrl != null) {
                if (!TextUtils.isEmpty(profilePhotoUrl)) {
                    final DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference();
                    final DatabaseReference users = reference.child("users");
                    final DatabaseReference myUser = users.child(userId);
                    myUser.child("online").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                final String myPresence = dataSnapshot.getValue().toString();
                                if (myPresence == "true") {
                                    presenceImageView.setImageDrawable(itemView.getResources()
                                            .getDrawable(R.drawable.ic_presence_status_online));
                                } else if (myPresence == "false") {
                                    presenceImageView.setImageDrawable(itemView.getResources()
                                            .getDrawable(R.drawable.ic_presence_status_offline));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(final DatabaseError databaseError) {

                        }
                    });
                    GlideApp.with(itemView.getContext())
                            .load(profilePhotoUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(profileImageView);

                }
            }
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference users = reference.child("users");
            final DatabaseReference myUser = users.child(userId);
            myUser.child("online").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final String myPresence = dataSnapshot.getValue().toString();
                        if (myPresence == "true") {
                            presenceImageView.setImageDrawable(itemView.getResources()
                                    .getDrawable(R.drawable.ic_presence_status_online));
                        } else if (myPresence == "false") {
                            presenceImageView.setImageDrawable(itemView.getResources()
                                    .getDrawable(R.drawable.ic_presence_status_offline));
                        }
                    }
                }

                @Override
                public void onCancelled(final DatabaseError databaseError) {

                }
            });
            profileImageView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(final View v) {
                    if (feedModel.getUid() != null) {
                        final Intent intent = new Intent(itemView.getContext(), ProfileActivity
                                .class);
                        intent.putExtra("user-reference", feedModel.getUid());
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
            final Date date = feedModel.getTimestamp();
            if (date != null) {
                final String dateString = new SimpleDateFormat().format(date);
                timeStamp.setText(dateString);
            }
            itemText.setText(feedModel.getFeedText());
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //Intent intent = new Intent(view.getContext(), FirestoreItemDetailActivity
                    // .class);
                    //intent.putExtra("key_post_id", snapshot.getId());
                    //view.getContext().startActivity(intent);
                    //postItemCardView.setBackgroundColor(Color.GREEN);
                    onFirestoreItemSelected.onFirestoreItemSelected(feedModel, itemView);
                }
            });

            expandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(@NonNull final View v) {
                    //creating a popup menu
                    final PopupMenu popup = new PopupMenu(v.getContext(), expandButton);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_item);
                    final FirebaseAuth auth = FirebaseUtil.getAuth();
                    final FirebaseUser user = auth.getCurrentUser();
                    final String currentUserId = user.getUid();
                    if (!TextUtils.equals(feedModel.getUid(), currentUserId)) {
                        popup.getMenu().getItem(1).setVisible(false);
                        //popup.getMenu().getItem(2).setVisible(true);
                    }

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(@NonNull final MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    break;
                                case R.id.delete:
                                    final FirebaseFirestore firestore = FirebaseUtil.getFirestore();
                                    final Task<Void> reference = firestore.collection(Utils.POSTS)
                                            .document(snapshot.getId())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {


                                                @Override
                                                public void onComplete(@NonNull final Task<Void>
                                                                               task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: ");
                                                    }
                                                }
                                            });
                                    break;
                                case R.id.follow:
                                    final DatabaseReference database = FirebaseDatabase
                                            .getInstance()
                                            .getReference("users");
                                    final DatabaseReference followedFollowersRef = database.child
                                            (feedModel
                                            .getUid()).child("followers");

                                    final Map<String, Object> data = new HashMap<>();
                                    data.put(currentUserId, true);
                                    followedFollowersRef.updateChildren(data);
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });
        }
    }
}
