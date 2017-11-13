package co.wasder.wasder.arch.data.source.firestoredb;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Ahmed AlAskalany on 11/8/2017.
 * Navigator
 */

public class FirebaseJobService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(final JobParameters job) {
        return false;
    }
}
