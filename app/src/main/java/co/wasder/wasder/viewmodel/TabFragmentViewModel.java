package co.wasder.wasder.viewmodel;

import android.arch.lifecycle.ViewModel;

import co.wasder.wasder.filter.PostsFilters;

/**
 * Created by Ahmed AlAskalany on 10/14/2017.
 * Wasder AB
 */

public class TabFragmentViewModel extends ViewModel {

    private PostsFilters mPostsFilters;

    public TabFragmentViewModel() {
        mPostsFilters = PostsFilters.getDefault();
    }

    public PostsFilters getFilters() {
        return mPostsFilters;
    }

    public void setFilters(PostsFilters mPostsFilters) {
        this.mPostsFilters = mPostsFilters;
    }
}
