package co.wasder.wasder.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import co.wasder.wasder.data.vo.Contributer;
import co.wasder.wasder.data.vo.Repo;
import co.wasder.wasder.data.vo.RepoSearchResult;
import co.wasder.wasder.data.vo.User;

/** Created by Ahmed AlAskalany on 11/13/2017. Navigator */
@Database(
    entities = {User.class, Repo.class, Contributer.class, RepoSearchResult.class},
    version = 1
)
public abstract class WasderDB extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract RepoDao repoDao();
}
