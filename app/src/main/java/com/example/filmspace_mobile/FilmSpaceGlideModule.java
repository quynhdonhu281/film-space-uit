package com.example.filmspace_mobile;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public class FilmSpaceGlideModule extends AppGlideModule {
    
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Set default request options for all Glide requests
        builder.setDefaultRequestOptions(
            new RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565) // Use less memory
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache all versions
                .timeout(30000) // 30 second timeout
        );
    }

    @Override
    public boolean isManifestParsingEnabled() {
        // Disable manifest parsing for faster initialization
        return false;
    }
}
