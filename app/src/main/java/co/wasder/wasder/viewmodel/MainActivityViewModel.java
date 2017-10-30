package co.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;

import co.wasder.wasder.filter.PostsFilters;

/**
 * Created by Ahmed AlAskalany on 10/11/2017.
 * Wasder AB
 */

public class MainActivityViewModel extends ViewModel {

    private boolean mIsSigningIn;
    private PostsFilters mPostsFilters;

    public MainActivityViewModel() {
        mIsSigningIn = false;
        mPostsFilters = PostsFilters.getDefault();
    }

    public boolean getIsSigningIn() {
        return mIsSigningIn;
    }

    public void setIsSigningIn(boolean mIsSigningIn) {
        this.mIsSigningIn = mIsSigningIn;
    }

    public PostsFilters getFilters() {
        return mPostsFilters;
    }

    public void setFilters(PostsFilters mPostsFilters) {
        this.mPostsFilters = mPostsFilters;
    }
}
