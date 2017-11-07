package co.wasder.wasder.ui.views;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
public class Header extends FrameLayout {

    public UserName userName;
    public TimeStamp timeStamp;
    public ExpandButton expandButton;
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
