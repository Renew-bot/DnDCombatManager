package com.example.dndcombatmanager.combat.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * Converts an OKLCH color (as used throughout the original design) to a Compose [Color].
 * [l] is lightness as a 0..1 fraction (matching e.g. "74%" -> 0.74f), [c] is chroma, [h] is hue in degrees.
 */
fun oklch(l: Float, c: Float, h: Float, alpha: Float = 1f): Color {
    val hRad = h * PI.toFloat() / 180f
    val a = c * cos(hRad)
    val b = c * sin(hRad)

    val l_ = l + 0.3963377774f * a + 0.2158037573f * b
    val m_ = l - 0.1055613458f * a - 0.0638541728f * b
    val s_ = l - 0.0894841775f * a - 1.2914855480f * b

    val l3 = l_ * l_ * l_
    val m3 = m_ * m_ * m_
    val s3 = s_ * s_ * s_

    val r = 4.0767416621f * l3 - 3.3077115913f * m3 + 0.2309699292f * s3
    val g = -1.2684380046f * l3 + 2.6097574011f * m3 - 0.3413193965f * s3
    val bl = -0.0041960863f * l3 - 0.7034186147f * m3 + 1.7076147010f * s3

    fun gamma(x: Float): Float {
        val xc = x.coerceIn(0f, 1f)
        return if (xc <= 0.0031308f) 12.92f * xc else 1.055f * xc.pow(1f / 2.4f) - 0.055f
    }

    return Color(gamma(r), gamma(g), gamma(bl), alpha.coerceIn(0f, 1f))
}
