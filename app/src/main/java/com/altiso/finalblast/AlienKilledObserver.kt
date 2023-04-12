package com.altiso.finalblast

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

interface AlienKilledObserver {
    fun onAlienKilled()
}
class Leaderboard : AlienKilledObserver {
    private var aliensKilled = 0

    override fun onAlienKilled() {
        aliensKilled++
    }

    fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.textSize = 50f
        canvas.drawText("Aliens tués: $aliensKilled", 20f, 60f, paint)
    }
}