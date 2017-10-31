package co.wasder.wasder.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.wasder.data.model.FirestoreItem;
import co.wasder.wasder.R;
import co.wasder.wasder.views.ItemImage;
import co.wasder.wasder.views.ItemText;
import co.wasder.wasder.views.ProfilePhoto;
import co.wasder.wasder.views.UserName;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */

public class FirestoreItemAdapter extends FirestoreRecyclerAdapter<FirestoreItem,
        FirestoreItemAdapter.FirestoreItemHolder> implements FirestoreItemsAdapter {

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
    protected void onBindViewHolder(FirestoreItemHolder holder, int position, FirestoreItem model) {
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

        @BindView(R.id.profilePhoto)
        ProfilePhoto itemProfilePhoto;

        @BindView(R.id.userName)
        UserName userName;

        @BindView(R.id.postItemCardView)
        CardView postItemCardView;

        @BindView(R.id.itemText)
        ItemText feedText;

        @BindView(R.id.itemImage)
        ItemImage itemImage;

        FirestoreItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnFirestoreItemSelected
                onFirestoreItemSelected) {

            final FirestoreItem firestoreItem = snapshot.toObject(FirestoreItem.class);
            Resources resources = itemView.getResources();
            // Load image
            String uuid = firestoreItem.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                Glide.with(itemView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(itemImage.getItemImageView());
                itemImage.makeVisible();
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
                                .into(itemProfilePhoto.getProfileImageView());
                    }
                }
            }
            userName.setText(firestoreItem.getName());
            feedText.getItemTextView().setText(firestoreItem.getFeedText());
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
        }
    }
}
