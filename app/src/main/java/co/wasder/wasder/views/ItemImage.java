package co.wasder.wasder.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */

public class ItemImage extends FrameLayout {

    private ImageView itemImageView;

    public ItemImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_image, this, true);
        this.setVisibility(GONE);
        itemImageView = findViewById(R.id.itemImageView);
    }

    public ImageView getItemImageView() {
        return itemImageView;
    }

    public void makeVisible() {
        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
        }
    }
}
