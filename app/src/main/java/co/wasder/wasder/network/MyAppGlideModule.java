package co.wasder.wasder.network;

import android.content.Context;
import android.support.annotation.Keep;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

/**
 * Created by Ahmed AlAskalany on 10/13/2017.
 * Wasder AB
 */
@Keep
@SuppressWarnings("WeakerAccess")
@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(final Context context, final Glide glide, final Registry registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader
                .Factory());
    }
}
