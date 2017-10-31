package co.wasder.wasder.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */

public class TimeStamp extends android.support.v7.widget.AppCompatTextView {

    public TimeStamp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setText("2h");
    }
}
