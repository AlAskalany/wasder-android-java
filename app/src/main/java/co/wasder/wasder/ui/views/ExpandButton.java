package co.wasder.wasder.ui.views;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.AttributeSet;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
public class ExpandButton extends android.support.v7.widget.AppCompatImageButton {

    public ExpandButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.setImageDrawable(getResources().getDrawable(R.drawable.mr_group_expand));
    }
}
