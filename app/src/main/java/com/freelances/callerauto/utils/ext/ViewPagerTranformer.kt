package com.freelances.callerauto.utils.ext
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import kotlin.math.abs

fun getPagerTransformer () :CompositePageTransformer {
    val compositePageTransformer = CompositePageTransformer()
    compositePageTransformer.addTransformer(MarginPageTransformer(100))
    compositePageTransformer.addTransformer { view, position ->
        val r = 1 - abs(position)
        view.scaleY = 0.8f + r * 0.2f
        val absPosition = abs(position)
        view.alpha = 1.0f - (1.0f - 0.3f) * absPosition
    }
    return compositePageTransformer
}