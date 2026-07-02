package com.example

import android.app.Application
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

class AuraApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        // Explicitly register custom ImageLoader with global Coil singleton to guarantee smooth GIF playback
        Coil.setImageLoader(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .build()
    }
}
