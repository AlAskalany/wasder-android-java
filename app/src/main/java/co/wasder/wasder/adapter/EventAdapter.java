package co.wasder.wasder.adapter;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import co.wasder.wasder.GlideApp;
import co.wasder.wasder.ProfileActivity;
import co.wasder.wasder.R;
import co.wasder.wasder.model.Event;
import co.wasder.wasder.model.FirestoreItem;
import co.wasder.wasder.model.User;
import co.wasder.wasder.views.EventView;
import co.wasder.wasder.views.FirestoreCollections;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
public class EventAdapter extends FirestoreRecyclerAdapter<Event, EventAdapter.EventHolder>
        implements EventsAdapter {

    public static final String TAG = "EventAdapter";
    public OnEventSelected mListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions
     */
    public EventAdapter(FirestoreRecyclerOptions options, OnEventSelected listener) {
        //noinspection unchecked
        super(options);
        mListener = listener;
    }

    @SuppressWarnings("unused")
    public static EventsAdapter newInstance(@NonNull LifecycleOwner lifecycleOwner, Query query,
                                            OnEventSelected listener) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<FirestoreItem>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, FirestoreItem.class)
                .build();
        return new EventAdapter(options, listener);
    }

    @SuppressWarnings("unused")
    public static EventsAdapter newInstance(Query query, OnEventSelected listener) {
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<FirestoreItem>()
                .setQuery(query, FirestoreItem.class)
                .build();
        return new EventAdapter(options, listener);
    }

    @Override
    protected void onBindViewHolder(final EventHolder holder, int position, Event model) {
        holder.bind(getSnapshots().getSnapshot(position), mListener);
    }

    @Override
    public EventHolder onCreateViewHolder(ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.item_event, group, false);
        return new EventHolder(view);
    }

    /**
     * Created by Ahmed AlAskalany on 10/13/2017.
     * Wasder AB
     */
    @Keep
    public static class EventHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "FirestoreItemHolder";
        public static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale
                .US);

        @BindView(R.id.feedView)
        public EventView eventView;
        public String tag;

        EventHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnEventSelected onEventSelected) {
            final Event event = snapshot.toObject(Event.class);

            String userId = event.getUid();
            CollectionReference users = FirebaseFirestore.getInstance()
                    .collection(FirestoreCollections.USERS);
            DocumentReference userReference = users.document(userId);
            Task<DocumentSnapshot> getUserTask = userReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            User user = task.getResult().toObject(User.class);
                            String userName = user.getDisplayName();
                            eventView.getHeader().getUserName().setText(userName);
                        }
                    });

            String title = event.getTitle();
            if (title != null) {
                eventView.getEventTitle().setText(title);
            }
            // Load image
            String uuid = null;
            uuid = event.getPhoto();
            if (!TextUtils.isEmpty(uuid)) {
                StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
                GlideApp.with(itemView.getContext())
                        .load(mImageRef)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(eventView.getItemImage().getItemImageView());
                eventView.getItemImage().makeVisible();
            }
            String profilePhotoUrl = event.getProfilePhoto();
            if (profilePhotoUrl != null) {
                if (!TextUtils.isEmpty(profilePhotoUrl)) {
                    GlideApp.with(itemView.getContext())
                            .load(profilePhotoUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(eventView.getProfilePhoto().getProfileImageView(userId));

                }
            }
            eventView.getProfilePhoto()
                    .getProfileImageView(userId)
                    .setOnClickListener(new View.OnClickListener() {


                        @Override
                        public void onClick(View v) {
                            if (event.getUid() != null) {
                                Intent intent = new Intent(itemView.getContext(), ProfileActivity
                                        .class);
                                intent.putExtra("user-reference", event.getUid());
                                itemView.getContext().startActivity(intent);
                            }
                        }
                    });
            eventView.getHeader().getUserName().setText(event.getName());
            Date date = event.getTimestamp();
            if (date != null) {
                String dateString = new SimpleDateFormat().format(date);
                eventView.getHeader().getTimeStamp().setText(dateString);
            }
            eventView.getItemText().getItemTextView().setText(event.getFeedText());
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(view.getContext(), FirestoreItemDetailActivity
                    // .class);
                    //intent.putExtra("key_post_id", snapshot.getId());
                    //view.getContext().startActivity(intent);
                    //postItemCardView.setBackgroundColor(Color.GREEN);
                    onEventSelected.onEventSelected(snapshot, itemView);
                }
            });

            eventView.getHeader().getExpandButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(v.getContext(), eventView.getHeader()
                            .getExpandButton());
                    //inflating menu from xml resource
                    popup.inflate(R.menu.menu_item);
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    String currentUserId = user.getUid();
                    if (!TextUtils.equals(event.getUid(), currentUserId)) {
                        popup.getMenu().getItem(1).setVisible(false);
                    }

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    break;
                                case R.id.delete:
                                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                                    Task<Void> reference = firestore.collection(FirestoreCollections.EVENTS)
                                            .document(snapshot.getId())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {


                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: ");
                                                    }
                                                }
                                            });
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
