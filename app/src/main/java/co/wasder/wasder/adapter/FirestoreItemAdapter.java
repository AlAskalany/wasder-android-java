package co.wasder.wasder.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.data.model.FirestoreItem;
import co.wasder.wasder.ProfileActivity;
import co.wasder.wasder.R;
import co.wasder.wasder.views.FeedView;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class FirestoreItemAdapter extends FirestoreRecyclerAdapter<FirestoreItem,
        FirestoreItemAdapter.FirestoreItemHolder> implements FirestoreItemsAdapter {

    private static final String TAG = "FirestoreItemAdapter";
    private OnFirestoreItemSelected mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions
     */
    FirestoreItemAdapter(FirestoreRecyclerOptions options, OnFirestoreItemSelected listener) {
        //noinspection unchecked
        super(options);
        mListener = listener;
    }

    @SuppressWarnings("unused")
    public static FirestoreItemsAdapter newInstance(@NonNull LifecycleOwner lifecycleOwner, Query
            query, OnFirestoreItemSelected listener) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<FirestoreItem>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, FirestoreItem.class)
                .build();
        return new FirestoreItemAdapter(options, listener);
    }

    @SuppressWarnings("unused")
    public static FirestoreItemsAdapter newInstance(Query query, OnFirestoreItemSelected listener) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<FirestoreItem>()
                .setQuery(query, FirestoreItem.class)
                .build();
        return new FirestoreItemAdapter(options, listener);
    }

    @Override
    protected void onBindViewHolder(final FirestoreItemHolder holder, int position, FirestoreItem
            model) {
        holder.bind(getSnapshots().getSnapshot(position), mListener);
    }

    @Override
    public FirestoreItemHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_firestore_item, group, false);
        return new FirestoreItemHolder(view);
    }

    /**
     * Created by Ahmed AlAskalany on 10/13/2017.
     * Wasder AB
     */
    public static class FirestoreItemHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "FirestoreItemHolder";
        private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale
                .US);

        @BindView(R.id.feedView)
        FeedView feedView;
        private String tag;

        FirestoreItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnFirestoreItemSelected
                onFirestoreItemSelected) {
            final FirestoreItem firestoreItem = snapshot.toObject(FirestoreItem.class);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Resources resources = itemView.getResources();

            // Load image
            String uuid = firestoreItem.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                Glide.with(itemView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(feedView.getItemImage().getItemImageView());
                feedView.getItemImage().makeVisible();
            }
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            Uri photoUri = null;
            if (user != null) {
                photoUri = user.getPhotoUrl();
                String photoUrl = null;
                if (photoUri != null) {
                    photoUrl = photoUri.toString();
                    if (!TextUtils.isEmpty(photoUrl)) {
                        Glide.with(itemView.getContext())
                                .load(photoUri)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(feedView.getProfilePhoto().getProfileImageView());
                    }
                }
            }
            feedView.getProfilePhoto()
                    .getProfileImageView()
                    .setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View v) {
                            if (firestoreItem.getUId() != null) {
                                Intent intent = new Intent(itemView.getContext(), ProfileActivity
                                        .class);
                                intent.putExtra("user-reference", firestoreItem.getUId());
                                itemView.getContext().startActivity(intent);
                            }
                        }
                    });
            feedView.getHeader().getUserName().setText(firestoreItem.getName());
            Date date = firestoreItem.getTimestamp();
            if (date != null) {
                String dateString = new SimpleDateFormat().format(date);
                feedView.getHeader().getTimeStamp().setText(dateString);
            }
            feedView.getItemText().getItemTextView().setText(firestoreItem.getFeedText());
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(view.getContext(), FirestoreItemDetailActivity
                    // .class);
                    //intent.putExtra("key_post_id", snapshot.getId());
                    //view.getContext().startActivity(intent);
                    //postItemCardView.setBackgroundColor(Color.GREEN);
                    onFirestoreItemSelected.onFirestoreItemSelected(snapshot, itemView);
                }
            });

            feedView.getHeader().getExpandButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(v.getContext(), feedView.getHeader()
                            .getExpandButton());
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_item);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    break;
                                case R.id.delete:
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
