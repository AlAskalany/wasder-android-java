package co.wasder.wasder.ui.views;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
public class ItemText extends FrameLayout {

    public TextView itemTextView;

    public ItemText(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_text, this, true);
        itemTextView = findViewById(R.id.itemTextView);
    }

    public TextView getItemTextView() {
        return itemTextView;
    }
}
