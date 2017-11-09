package co.wasder.wasder.ui.views;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
public class EventView extends FrameLayout {

    public ProfilePhoto profilePhoto;
    public TextView eventTitle;
    public Header header;
    public ItemText itemText;
    public ItemImage itemImage;
    public Actions actions;

    public EventView(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R.layout.event, this, true);
        profilePhoto = findViewById(R.id.profilePhoto);
        eventTitle = findViewById(R.id.eventTitle);
        header = findViewById(R.id.header);
        itemText = findViewById(R.id.itemText);
        itemImage = findViewById(R.id.itemImage);
        actions = findViewById(R.id.actions);
    }

    public ProfilePhoto getProfilePhoto() {
        return profilePhoto;
    }

    public TextView getEventTitle() {
        return eventTitle;
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
