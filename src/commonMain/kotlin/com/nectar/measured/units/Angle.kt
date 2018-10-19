@file:Suppress("NOTHING_TO_INLINE")

package com.nectar.measured.units

import kotlin.math.PI

/**
 * Created by Nicholas Eddy on 10/19/17.
 */

interface Angle

val radians = Unit<Angle>(" rad")
val degrees = Unit<Angle>("°", PI / 180)

val Int.   radians: Measure<Angle> get() = this * com.nectar.measured.units.radians
val Float. radians: Measure<Angle> get() = this * com.nectar.measured.units.radians
val Long.  radians: Measure<Angle> get() = this * com.nectar.measured.units.radians
val Double.radians: Measure<Angle> get() = this * com.nectar.measured.units.radians

val Int.   degrees: Measure<Angle> get() = this * com.nectar.measured.units.degrees
val Float. degrees: Measure<Angle> get() = this * com.nectar.measured.units.degrees
val Long.  degrees: Measure<Angle> get() = this * com.nectar.measured.units.degrees
val Double.degrees: Measure<Angle> get() = this * com.nectar.measured.units.degrees

inline fun sin  (angle : Measure<Angle>        ) = kotlin.math.sin  (angle  `in` radians)
inline fun cos  (angle : Measure<Angle>        ) = kotlin.math.cos  (angle  `in` radians)
inline fun tan  (angle : Measure<Angle>        ) = kotlin.math.tan  (angle  `in` radians)
inline fun asin (value : Double                ) = kotlin.math.asin (value              ).radians
inline fun acos (value : Double                ) = kotlin.math.acos (value              ).radians
inline fun atan (value : Double                ) = kotlin.math.atan (value              ).radians
inline fun atan2(value1: Double, value2: Double) = kotlin.math.atan2(value1, value2     ).radians
inline fun sinh (angle : Measure<Angle>        ) = kotlin.math.sinh (angle  `in` radians)
inline fun cosh (angle : Measure<Angle>        ) = kotlin.math.cosh (angle  `in` radians)
inline fun tanh (angle : Measure<Angle>        ) = kotlin.math.tanh (angle  `in` radians)
inline fun asinh(value : Double                ) = kotlin.math.asinh(value              ).radians
inline fun acosh(value : Double                ) = kotlin.math.acosh(value              ).radians
inline fun atanh(value : Double                ) = kotlin.math.atanh(value              ).radians