package com.github.boybeak.selfslideapp

import android.graphics.Color
import kotlin.random.Random

fun randomColor(): Int {
    val random = Random(System.currentTimeMillis())
    return Color.rgb(
        random.nextInt(0, 256),
        random.nextInt(0, 256),
        random.nextInt(0, 256)
    )
}