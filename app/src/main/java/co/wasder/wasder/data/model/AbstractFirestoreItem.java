package co.wasder.wasder.data.model;

import java.util.Date;

/**
 * Created by Ahmed AlAskalany on 11/3/2017.
 * Navigator
 */

public abstract class AbstractFirestoreItem {

    private String photo;

    public abstract String getName();

    public abstract String getMessage();

    public abstract String getUid();

    public abstract String getPhoto();

    public abstract String getProfilePhoto();

    public abstract Date getTimestamp();

    public abstract String getFeedText();
}
