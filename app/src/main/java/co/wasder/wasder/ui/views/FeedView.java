package co.wasder.wasder.ui.views;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
public class FeedView extends FrameLayout {

    public ProfilePhoto profilePhoto;
    public Header header;
    public ItemText itemText;
    public ItemImage itemImage;
    public Actions actions;

    public FeedView(@NonNull final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R.layout.feed, this, true);
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
