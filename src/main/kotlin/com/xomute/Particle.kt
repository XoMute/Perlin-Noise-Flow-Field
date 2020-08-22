package com.xomute

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import java.lang.IndexOutOfBoundsException
import kotlin.math.floor

class Particle(var pos: Vector2, val color: Color) {
    private var vel = Vector2(0f, 0f)
    private var acc = Vector2(0f, 0f)
    private var maxSpeed = 2f
    private var prevPos = Vector2(pos)

    fun update() {
        vel = vel.add(acc)
        vel.limit(maxSpeed)
        pos = pos.add(vel)
        acc.x = 0f
        acc.y = 0f
    }

    private fun applyForce(force: Vector2) {
        acc.add(force)
    }

    fun show(renderer: ShapeRenderer) {
        renderer.color = color
        renderer.line(pos, prevPos)
        updatePrevPos()
    }

    private fun updatePrevPos() {
        prevPos.set(pos)
    }

    fun edges() {
        if (pos.x > WIDTH) {
            pos.x = 0f
            updatePrevPos()
        }
        if (pos.x < 0f) {
            pos.x = WIDTH.toFloat() - 0.1f
            updatePrevPos()
        }
        if (pos.y > HEIGHT) {
            pos.y = 0f
            updatePrevPos()
        }
        if (pos.y < 0f) {
            pos.y = HEIGHT.toFloat() - 0.1f
            updatePrevPos()
        }
    }

    fun follow(vectors: Array<Vector2>, cols: Int) {
        // todo: remove this hack
        if (pos.x == WIDTH.toFloat()) {
            pos.x -= 0.1f
        }
        if (pos.y == HEIGHT.toFloat()) {
            pos.y -= 0.1f
        }
        val x = floor(pos.x / scl).toInt()
        val y = floor(pos.y / scl).toInt()
        val index = x + y * cols
        try {

            val force = vectors[index]
            this.applyForce(force)

        } catch (e: IndexOutOfBoundsException) {
            println()
        }
    }
}
