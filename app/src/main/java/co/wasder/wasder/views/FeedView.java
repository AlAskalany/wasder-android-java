package co.wasder.wasder.views;

import android.view.View;
import android.view.ViewGroup;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */

public class FeedView extends View {

    private ProfilePhoto profilePhoto;
    private Header header;
    private ItemText itemText;
    private ItemImage itemImage;
    private Actions actions;

    public FeedView(ViewGroup group) {
        super(group.getContext());
        inflate(group.getContext(), R.layout.item_firestore_item, group);
        profilePhoto = findViewById(R.id.profilePhoto);
        header = findViewById(R.id.header);
        itemText = findViewById(R.id.itemText);
        itemImage = findViewById(R.id.itemImage);
        actions = findViewById(R.id.actions);
    }

    public ProfilePhoto getProfilePhoto() {
        return profilePhoto;
    }

    public Header getHeader() {
        return header;
    }

    public ItemText getItemText() {
        return itemText;
    }

    public ItemImage getItemImage() {
        return itemImage;
    }

    public Actions getActions() {
        return actions;
    }
}
