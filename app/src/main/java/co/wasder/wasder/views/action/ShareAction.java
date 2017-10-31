package co.wasder.wasder.views.action;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */

public class ShareAction extends FrameLayout {

    public ShareAction(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.button_share, this, true);
    }
}
