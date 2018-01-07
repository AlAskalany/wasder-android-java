package co.wasder.data.vo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Ahmed AlAskalany on 11/13/2017.
 * Navigator
 */
@Entity
public class User {

    @NonNull
    @PrimaryKey
    public String id;
}