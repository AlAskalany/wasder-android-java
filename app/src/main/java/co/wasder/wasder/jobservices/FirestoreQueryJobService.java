package co.wasder.wasder.jobservices;

import android.support.annotation.Keep;

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

    public Thread mThread;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        @SuppressWarnings("unused") Runnable mRunnable = mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    @SuppressWarnings("unused") String collectionName = null;
                    @SuppressWarnings("unused") Query query = firestore.collection("restaurants");
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
    public boolean onStopJob(JobParameters jobParameters) {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        return true;
    }
}
