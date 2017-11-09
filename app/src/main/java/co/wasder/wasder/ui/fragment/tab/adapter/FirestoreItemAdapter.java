package co.wasder.wasder.ui.fragment.tab.adapter;

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

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.wasder.R;
import co.wasder.wasder.data.model.FirestoreItem;
import co.wasder.wasder.data.model.User;
import co.wasder.wasder.network.GlideApp;
import co.wasder.wasder.ui.activity.detail.ProfileActivity;
import co.wasder.wasder.ui.views.FeedView;
import co.wasder.wasder.ui.views.FirestoreCollections;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class FirestoreItemAdapter extends FirestoreRecyclerAdapter<FirestoreItem,
        FirestoreItemAdapter.FirestoreItemHolder> implements FirestoreItemsAdapter {

    public static final String TAG = "FirestoreItemAdapter";
    public OnFirestoreItemSelected mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions
     */
    public FirestoreItemAdapter(final FirestoreRecyclerOptions options, final OnFirestoreItemSelected
            listener) {
        //noinspection unchecked
        super(options);
        mListener = listener;
    }

    @SuppressWarnings("unused")
    public static FirestoreItemsAdapter newInstance(@NonNull final LifecycleOwner lifecycleOwner, final Query
            query, final OnFirestoreItemSelected listener) {
        return Adapters.PostAdapter(lifecycleOwner, query, listener);
    }

    @SuppressWarnings("unused")
    public static FirestoreItemsAdapter newInstance(final Query query, final OnFirestoreItemSelected listener) {
        final FirestoreRecyclerOptions<FirestoreItem> options = new FirestoreRecyclerOptions.Builder<FirestoreItem>()
                .setQuery(query, FirestoreItem.class)
                .build();
        return new FirestoreItemAdapter(options, listener);
    }

    @Override
    public void onBindViewHolder(final FirestoreItemHolder holder, final int position, final FirestoreItem
            model) {
        holder.bind(getSnapshots().getSnapshot(position), mListener);
    }

    @Override
    public FirestoreItemHolder onCreateViewHolder(final ViewGroup group, final int viewType) {
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

        @BindView(R.id.feedView)
        public FeedView feedView;
        public String tag;

        public FirestoreItemHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnFirestoreItemSelected
                onFirestoreItemSelected) {
            final FirestoreItem firestoreItem = snapshot.toObject(FirestoreItem.class);
            final String userId = firestoreItem.getUid();
            final DocumentReference userReference = getAuthorDocumentReference(userId);
            final Task<DocumentSnapshot> getUserTask = userReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                        @Override
                        public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                            final User user = task.getResult().toObject(User.class);
                            final String userName = user.getDisplayName();
                            feedView.getHeader().getUserName().setText(userName);
                        }
                    });

            // Load image
            String uuid = null;
            uuid = firestoreItem.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                GlideApp.with(itemView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(feedView.getItemImage().getItemImageView());
                feedView.getItemImage().makeVisible();
            }
            final String profilePhotoUrl = firestoreItem.getProfilePhoto();
            if (profilePhotoUrl != null) {
                if (!TextUtils.isEmpty(profilePhotoUrl)) {
                    GlideApp.with(itemView.getContext())
                            .load(profilePhotoUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(feedView.getProfilePhoto().getProfileImageView(userId));

                }
            }
            feedView.getProfilePhoto()
                    .getProfileImageView(userId)
                    .setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(final View v) {
                            if (firestoreItem.getUid() != null) {
                                final Intent intent = new Intent(itemView.getContext(), ProfileActivity
                                        .class);
                                intent.putExtra("user-reference", firestoreItem.getUid());
                                itemView.getContext().startActivity(intent);
                            }
                        }
                    });
            final Date date = firestoreItem.getTimestamp();
            if (date != null) {
                final String dateString = new SimpleDateFormat().format(date);
                feedView.getHeader().getTimeStamp().setText(dateString);
            }
            feedView.getItemText().getItemTextView().setText(firestoreItem.getFeedText());
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    //Intent intent = new Intent(view.getContext(), FirestoreItemDetailActivity
                    // .class);
                    //intent.putExtra("key_post_id", snapshot.getId());
                    //view.getContext().startActivity(intent);
                    //postItemCardView.setBackgroundColor(Color.GREEN);
                    onFirestoreItemSelected.onFirestoreItemSelected(firestoreItem, itemView);
                }
            });

            feedView.getHeader().getExpandButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    //creating a popup menu
                    final PopupMenu popup = new PopupMenu(v.getContext(), feedView.getHeader()
                            .getExpandButton());
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_item);
                    final FirebaseAuth auth = FirebaseAuth.getInstance();
                    final FirebaseUser user = auth.getCurrentUser();
                    final String currentUserId = user.getUid();
                    if (!TextUtils.equals(firestoreItem.getUid(), currentUserId)) {
                        popup.getMenu().getItem(1).setVisible(false);
                        //popup.getMenu().getItem(2).setVisible(true);
                    }

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(final MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    break;
                                case R.id.delete:
                                    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    final Task<Void> reference = firestore.collection
                                            (FirestoreCollections.POSTS)
                                            .document(snapshot.getId())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {


                                                @Override
                                                public void onComplete(@NonNull final Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: ");
                                                    }
                                                }
                                            });
                                    break;
                                case R.id.follow:
                                    final DatabaseReference database = FirebaseDatabase.getInstance()
                                            .getReference("users");
                                    final DatabaseReference followedFollowersRef = database.child
                                            (firestoreItem
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

    @NonNull
    private static DocumentReference getAuthorDocumentReference(final String userId) {
        final CollectionReference users = FirebaseFirestore.getInstance()
                .collection(FirestoreCollections.USERS);
        return users.document(userId);
    }
}
