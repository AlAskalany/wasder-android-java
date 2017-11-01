package co.wasder.wasder.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */

public class EventView extends FrameLayout {

    private ProfilePhoto profilePhoto;
    private TextView eventTitle;
    private Header header;
    private ItemText itemText;
    private ItemImage itemImage;
    private Actions actions;

    public EventView(Context context, AttributeSet attributeSet) {
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
