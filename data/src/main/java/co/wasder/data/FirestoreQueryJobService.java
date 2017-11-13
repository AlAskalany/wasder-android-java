package co.wasder.data;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * Created by Ahmed AlAskalany on 10/18/2017.
 * Navigator
 */
@Keep
public class FirestoreQueryJobService extends JobService {

    @Nullable
    private Thread mThread;

    @Override
    public boolean onStartJob(@NonNull final JobParameters jobParameters) {
        @SuppressWarnings("unused") final Runnable mRunnable = mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    @SuppressWarnings("unused") final String collectionName = null;
                    @SuppressWarnings("unused") final Query query = firestore.collection("restaurants");
                } finally {
                    //Tell the framework that the job has completed and doesn't needs to be
                    // reschedule
                    jobFinished(jobParameters, false);
                }
            }
        });
        mThread.start();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters jobParameters) {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        return true;
    }
}
