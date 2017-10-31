package co.wasder.wasder.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */

public class Header extends FrameLayout {

    private UserName userName;
    private TimeStamp timeStamp;
    private ExpandButton expandButton;
    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_header, this, true);
        userName = findViewById(R.id.userName);
        timeStamp = findViewById(R.id.timeStamp);
        expandButton = findViewById(R.id.expandButton);
    }

    public UserName getUserName() {
        return userName;
    }

    public TimeStamp getTimeStamp() {
        return timeStamp;
    }

    public ExpandButton getExpandButton() {
        return expandButton;
    }
}
