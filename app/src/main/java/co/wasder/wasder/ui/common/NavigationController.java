package co.wasder.wasder.ui.common;

import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import co.wasder.wasder.R;
import co.wasder.wasder.ui.WasderActivity;

/** Created by Ahmed AlAskalany on 1/13/2018. Navigator */
public class NavigationController {
    private final FragmentManager fragmentManager;
    private int containerId;

    @Inject
    public NavigationController(WasderActivity wasderActivity) {
        this.containerId = R.id.container;
        this.fragmentManager = wasderActivity.getSupportFragmentManager();
    }

    public void navigateToSearch() {}
}
