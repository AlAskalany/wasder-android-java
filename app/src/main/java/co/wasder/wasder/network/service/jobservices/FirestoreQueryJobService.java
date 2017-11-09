package co.wasder.wasder.network.service.jobservices;

import android.support.annotation.Keep;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import co.wasder.wasder.Util.FirestoreItemUtil;


/**
 * Created by Ahmed AlAskalany on 10/18/2017.
 * Navigator
 */
@Keep
public class FirestoreQueryJobService extends JobService {

    public Thread mThread;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        @SuppressWarnings("unused") final Runnable mRunnable = mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FirebaseFirestore firestore = FirestoreItemUtil.getFirestore();
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
