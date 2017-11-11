package co.wasder.wasder.ui.views;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
public class UserName extends android.support.v7.widget.AppCompatTextView {

    public UserName(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        setText("User Name");
    }
}
