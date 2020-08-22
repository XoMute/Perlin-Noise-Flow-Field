package com.xomute

import com.badlogic.gdx.math.MathUtils.random
import kotlin.math.cos
import kotlin.math.floor

object Noise {

    val PERLIN_YWRAPB = 4
    val PERLIN_YWRAP = 1 shl PERLIN_YWRAPB
    val PERLIN_ZWRAPB = 8
    val PERLIN_ZWRAP = 1 shl PERLIN_ZWRAPB
    val PERLIN_SIZE = 4095

    val perlin_octaves = 4 // default to medium smooth
    val perlin_amp_falloff = 0.5f // 50% reduction/octave

    val scaled_cosine = { i: Double -> 0.5 * (1.0 - cos(i * Math.PI)) }

    val perlin: MutableList<Float> = mutableListOf() // will be initialized lazily by noise() or noiseSeed()

    fun noise(_x: Float, _y: Float = 0f, _z: Float = 0f): Float {
        if (perlin.isEmpty()) {
            for (i in 0..PERLIN_SIZE) {
                perlin.add(random())
            }
        }

        var x = _x
        var y = _y
        var z = _z

        if (x < 0) {
            x = -x
        }
        if (y < 0) {
            y = -y
        }
        if (z < 0) {
            z = -z
        }

        var xi = floor(x).toInt()
        var yi = floor(y).toInt()
        var zi = floor(z).toInt()
        var xf = x.toDouble() - xi
        var yf = y.toDouble() - yi
        var zf = z.toDouble() - zi

        var rxf: Float
        var ryf: Float

        var r = 0f
        var ampl = 0.5f

        var n1: Float
        var n2: Float
        var n3: Float

        for (o in 0 until perlin_octaves) {

            var of = xi + (yi shl PERLIN_YWRAPB) + (zi shl PERLIN_ZWRAPB)

            rxf = scaled_cosine(xf).toFloat()
            ryf = scaled_cosine(yf).toFloat()

            n1 = perlin[of and PERLIN_SIZE]
            n1 += rxf * (perlin[(of + 1) and PERLIN_SIZE] - n1)
            n2 = perlin[(of + PERLIN_YWRAP) and PERLIN_SIZE]
            n2 += rxf * (perlin[(of + PERLIN_YWRAP + 1) and PERLIN_SIZE] - n2)
            n1 += ryf * (n2 - n1)

            of += PERLIN_ZWRAP
            n2 = perlin[of and PERLIN_SIZE]
            n2 += rxf * (perlin[(of + 1) and PERLIN_SIZE] - n2)
            n3 = perlin[(of + PERLIN_YWRAP) and PERLIN_SIZE]
            n3 += rxf * (perlin[(of + PERLIN_YWRAP + 1) and PERLIN_SIZE] - n3)
            n2 += ryf * (n3 - n2)

            n1 += scaled_cosine(zf).toFloat() * (n2 - n1)

            r += n1 * ampl
            ampl *= perlin_amp_falloff
            xi = xi shl 1
            xf *= 2
            yi = yi shl 1
            yf *= 2
            zi = zi shl 1
            zf *= 2

            if (xf >= 1.0) {
                xi++
                xf--
            }
            if (yf >= 1.0) {
                yi++
                yf--
            }
            if (zf >= 1.0) {
                zi++
                zf--
            }
        }
        return r
    }
}
