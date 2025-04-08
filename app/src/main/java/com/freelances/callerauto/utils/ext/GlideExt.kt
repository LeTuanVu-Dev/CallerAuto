package com.freelances.callerauto.utils.ext

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import androidx.annotation.DimenRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadThumbnail(
    context: Context, path: String, @DimenRes cornerRadiusDp: Int = 0
) {
    val options: RequestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .apply(RequestOptions().transform(CenterCrop()))
        .transform(CenterCrop(), RoundedCorners(cornerRadiusDp))

    Glide.with(context)
        .load(path)
        .apply(options)
        .transition(DrawableTransitionOptions.withCrossFade(300))
        .into(this)
}

enum class Scale {
    FIT, FILL
}

@SuppressLint("CheckResult")
fun ImageView.loadImage(
    src: Int,
    scaleType: Scale = Scale.FIT,
) {
    val requestOptions = RequestOptions().apply {
        when (scaleType) {
            Scale.FIT -> fitCenter()
            Scale.FILL -> centerCrop()
        }
    }

    Glide.with(this.context)
        .load(src)
        .apply(requestOptions)
        .transition(DrawableTransitionOptions.withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}
