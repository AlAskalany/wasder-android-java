package co.wasder.data.base;

import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Created by Ahmed AlAskalany on 11/3/2017.
 * Navigator
 */

public abstract class BaseModel {

    private String photo;

    public abstract String getName();

    @Nullable
    public abstract String getMessage();

    public abstract String getUid();

    public abstract String getPhoto();

    public abstract String getProfilePhoto();

    public abstract Date getTimestamp();

    public abstract String getFeedText();
}
