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
    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_header, this, true);
        userName = findViewById(R.id.userName);
    }

    public UserName getUserName() {
        return userName;
    }
}
