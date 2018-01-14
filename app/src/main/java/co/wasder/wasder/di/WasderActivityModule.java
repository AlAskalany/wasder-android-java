package co.wasder.wasder.di;

import co.wasder.wasder.ui.WasderActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/** Created by Ahmed AlAskalany on 1/13/2018. Navigator */
@Module
public abstract class WasderActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuilderModule.class)
    abstract WasderActivity contributeMainActivity();
}
