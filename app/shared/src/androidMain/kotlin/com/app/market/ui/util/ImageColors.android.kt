package com.app.market.ui.util

import coil3.Image
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap

internal actual fun ImageRequest.Builder.allowColorSampling(): ImageRequest.Builder =
    allowHardware(false)

internal actual fun sampleImagePixels(image: Image, size: Int): IntArray = runCatching {
    // Request the tiny bitmap directly.  Converting at the source dimensions first can
    // transiently allocate several MB for a large cover only to keep 24 x 24 pixels.
    val sample = image.toBitmap(size, size)
    IntArray(size * size).also { sample.getPixels(it, 0, size, 0, 0, size, size) }
}.getOrDefault(IntArray(0))
