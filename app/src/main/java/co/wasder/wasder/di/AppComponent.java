package co.wasder.wasder.di;

import android.app.Application;

import javax.inject.Singleton;

import co.wasder.wasder.WasderApplication;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/** Created by Ahmed AlAskalany on 1/13/2018. Navigator */
@Singleton
@Component(modules = {AndroidInjectionModule.class, AppModule.class, WasderActivityModule.class})
public interface AppComponent {
    void inject(WasderApplication wasderApplication);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
