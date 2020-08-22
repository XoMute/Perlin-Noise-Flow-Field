package com.xomute

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.ScreenUtils
import com.xomute.Noise.noise
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.color
import ktx.graphics.use
import kotlin.math.*


val WIDTH = LwjglApplicationConfiguration.getDesktopDisplayMode().width
val HEIGHT = LwjglApplicationConfiguration.getDesktopDisplayMode().height
const val scl = 20f
val HSV = mutableListOf<Color>()

fun main() {
    val config = LwjglApplicationConfiguration().apply {
        width = WIDTH
        height = HEIGHT
        fullscreen = true
    }
    LwjglApplication(PerlinNoise(), config)
}

class PerlinNoise : KtxGame<KtxScreen>() {

    private val particles = mutableListOf<Particle>()
    private lateinit var flowField: Array<Vector2>

    private lateinit var renderer: ShapeRenderer
    private lateinit var frameRate: FrameRate
    private var inc = 0.1f
    private var zinc = 0.003f
    private var cols = floor(WIDTH / scl).toInt()
    private var rows = floor(HEIGHT / scl).toInt()

    var zoff = 0f

    override fun create() {
        renderer = ShapeRenderer()
        frameRate = FrameRate()
        flowField = Array(cols * rows) { Vector2(0f, 0f) }
        for (i in 0..2000) {
            particles.add(
                Particle(
                    Vector2(Math.random().toFloat() * WIDTH, Math.random().toFloat() * HEIGHT),
                    color(159 / 255f, 31 / 255f, 239 / 255f, .1f)
                )
            )
        }

        for (i in 0..2000) {
            particles.add(
                Particle(
                    Vector2(Math.random().toFloat() * WIDTH, Math.random().toFloat() * HEIGHT),
                    color(191 / 255f, 0f, 252 / 255f, .1f)
                )
            )
        }

        for (i in 0..2000) {
            particles.add(
                Particle(
                    Vector2(Math.random().toFloat() * WIDTH, Math.random().toFloat() * HEIGHT),
                    color(234 / 255f, 7 / 255f, 1f, .1f)
                )
            )
        }

        for (i in 0..2000) {
            particles.add(
                Particle(
                    Vector2(Math.random().toFloat() * WIDTH, Math.random().toFloat() * HEIGHT),
                    color(.99f, 204 / 255f, 3 / 255f, .1f)
                )
            )
        }
        for (i in 0..2000) {
            particles.add(
                Particle(
                    Vector2(Math.random().toFloat() * WIDTH, Math.random().toFloat() * HEIGHT),
                    color(230 / 255f, 185 / 255f, 0f, .1f)
                )
            )
        }

        hashHSV()

//        clearScreen(1f, 1f, 1f, 1f)
        clearScreen(0f, 0f, 0f, 1f)
    }

    override fun render() {
        handleInput()
        logic()
        draw()
        frameRate.render()
    }

    fun handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            inc += 0.01f
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            inc -= 0.01f
        } else if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            zinc += 0.005f
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            zinc -= 0.005f
        } else if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            dispose()
        } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            screenshot()
        }
    }

    fun logic() {
    }

    fun draw() {
        var yoff = 0f
        renderer.use(ShapeRenderer.ShapeType.Line) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            for (x in 0 until cols) {
                var xoff = 0f
                for (y in 0 until rows) {
                    val angle = noise(xoff, yoff, zoff) * PI.toFloat() * 4

                    val v = Vector2(cos(angle), sin(angle))
                    flowField[x + y * cols] = v
                    v.setLength(0.8f)
                    xoff += inc
                }
                yoff += inc
            }
            zoff += zinc
            it.color = color(0f, 0f, 0f, 1f)
            particles.forEach { pt -> pt.follow(flowField, cols - 1); pt.update(); pt.show(renderer); pt.edges(); }
        }
    }

    override fun dispose() {
        renderer.dispose()
        frameRate.dispose()
        super.dispose()
    }

    fun screenshot() {
        val pixels: ByteArray =
            ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, true)

        for (i in 4 until pixels.size step 4) {
            pixels[i - 1] = 255.toByte()
        }

        val pixmap = Pixmap(Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, Pixmap.Format.RGBA8888)
        BufferUtils.copy(pixels, 0, pixmap.pixels, pixels.size)
        PixmapIO.writePNG(FileHandle("mypixmap${(Math.random() * 10000).toInt()}.png"), pixmap)
        pixmap.dispose()
    }

    private fun hashHSV() {
        for (i in 0 until 360) {
            val h = i
            var tempC = 1
            var tempX = tempC * (1 - abs((h / 60) % 2 - 1))
            var r = 0f
            var g = 0f
            var b = 0f
            when {
                h < 60 -> {
                    r = tempC.toFloat(); g = tempX.toFloat()
                }
                h < 120 -> {
                    r = tempX.toFloat(); g = tempC.toFloat()
                }
                h < 180 -> {
                    g = tempC.toFloat(); b = tempX.toFloat()
                }
                h < 240 -> {
                    g = tempX.toFloat(); b = tempC.toFloat()
                }
                h < 300 -> {
                    r = tempX.toFloat(); b = tempC.toFloat()
                }
                h < 360 -> {
                    r = tempC.toFloat(); b = tempX.toFloat()
                }
            }
            HSV.add(color(r, g, b, .1f))
        }
    }
}
