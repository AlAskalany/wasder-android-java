package co.wasder.wasder.views;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import co.wasder.wasder.R;

/**
 * Created by Ahmed AlAskalany on 10/31/2017.
 * Navigator
 */
@Keep
public class ProfilePhoto extends FrameLayout {

    ImageView profileImageView;

    public ProfilePhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_profile_photo, this, true);
        profileImageView = findViewById(R.id.itemProfileImageView);
    }

    public ImageView getProfileImageView() {
        return profileImageView;
    }
}
