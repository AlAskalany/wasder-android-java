package co.wasder.wasder.jobservices;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * Created by Ahmed AlAskalany on 10/18/2017.
 * Navigator
 */

public class FirestoreQueryJobService extends JobService {

    private Thread mThread;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Runnable mRunnable = mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    String collectionName = null;
                    Query query = firestore.collection("restaurants");
                } finally {
                    //Tell the framework that the job has completed and doesnot needs to be
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
