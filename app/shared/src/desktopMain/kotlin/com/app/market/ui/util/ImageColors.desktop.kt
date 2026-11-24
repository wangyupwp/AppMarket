package com.app.market.ui.util

import coil3.Image
import coil3.request.ImageRequest
import coil3.toBitmap

internal actual fun ImageRequest.Builder.allowColorSampling(): ImageRequest.Builder = this

internal actual fun sampleImagePixels(image: Image, size: Int): IntArray = runCatching {
    // Keep color extraction bounded: the previous full-size conversion created a large
    // temporary bitmap before sampling only a 24 x 24 grid.
    val bitmap = image.toBitmap(size, size)
    IntArray(size * size) { index ->
        val sampleX = index % size
        val sampleY = index / size
        val sourceX = ((sampleX + 0.5f) * bitmap.width / size).toInt().coerceAtMost(bitmap.width - 1)
        val sourceY = ((sampleY + 0.5f) * bitmap.height / size).toInt().coerceAtMost(bitmap.height - 1)
        bitmap.getColor(sourceX, sourceY)
    }
}.getOrDefault(IntArray(0))
