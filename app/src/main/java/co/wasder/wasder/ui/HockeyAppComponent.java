package co.wasder.wasder.ui;

import android.support.v7.app.AppCompatActivity;

import net.hockeyapp.android.FeedbackManager;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.metrics.MetricsManager;

import java.util.HashMap;
import java.util.Map;

public class HockeyAppComponent {

    public HockeyAppComponent() {}

    void unregisterManagers() {
        UpdateManager.unregister();
    }

    void setupHockeyApp(AppCompatActivity activity) {
        MetricsManager.register(activity.getApplication());
        MetricsManager.trackEvent("WasderActivity");
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("Property1", "Value1");
        final Map<String, Double> measurements = new HashMap<String, Double>();
        measurements.put("Measurement1", 1.0);
        MetricsManager.trackEvent("YOUR_EVENT_NAME", properties, measurements);
        // Remove this for store builds!
        UpdateManager.register(activity);
        FeedbackManager.register(activity);
    }
}
