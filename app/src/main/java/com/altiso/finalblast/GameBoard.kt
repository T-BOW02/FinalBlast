package com.altiso.finalblast
import android.graphics.Color
import Alien
import AlienSwarm
import Projectile
import Spaceship
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager

@SuppressLint("ClickableViewAccessibility")
class GameBoard(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var thread: GameThread
    private lateinit var spaceshipBitmap: Bitmap
    private lateinit var backgroundBitmap: Bitmap
    private val screenHeight: Int
    private val screenWidth: Int
    private val projectiles = mutableListOf<Projectile>()
    private lateinit var spaceship: Spaceship
    private lateinit var alienSwarm: AlienSwarm
    private var aliensKilled = 0
    private var shootTimer = 0L

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
        spaceshipBitmap = BitmapFactory.decodeResource(resources, R.drawable.spaceship)
        backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.background)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    if (event.x < spaceship.x) {
                        spaceship.moveLeft()
                    } else {
                        spaceship.moveRight()
                    }
                }
            }
            true
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!thread.running) {
            thread = GameThread(holder, this)
            thread.running = true
            thread.start()
        }
        backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.background)
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, true)
        alienSwarm = AlienSwarm(context, screenWidth = width, screenHeight = height)

        spaceship = Spaceship(0.0, 0.0, width, height, context)
        spaceship.y = screenHeight * 0.75f
        spaceship.x = screenWidth * 0.4f

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.running = false
                thread.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val paint = Paint()

        canvas.drawBitmap(backgroundBitmap, 0f, 0f, paint)
        alienSwarm.draw(canvas)
        spaceship.draw(canvas)

        val projectilesToRemove = mutableListOf<Projectile>()
        val aliensToRemove = mutableListOf<Alien>()

        projectiles.forEach { projectile ->
            projectile.draw(canvas)

            alienSwarm.aliens.forEach { alien ->
                if (alien.isCollidingWith(projectile)) {
                    projectilesToRemove.add(projectile)
                    if (alien.lives <= 0) {
                        aliensToRemove.add(alien)
                    }
                }
            }
            // Dessinez le leaderboard
            paint.color = Color.WHITE
            paint.textSize = 50f
            canvas.drawText("Aliens tués: $aliensKilled", 20f, 60f, paint)
        }

        projectiles.removeAll(projectilesToRemove)
        alienSwarm.aliens.removeAll(aliensToRemove)
        projectilesToRemove.clear()
        aliensToRemove.clear()
    }

    fun fireProjectile() {
        val projectile = spaceship.shoot()
        projectiles.add(projectile)
    }

    fun update() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - shootTimer >= 500) {
            fireProjectile()
            shootTimer = currentTime
        }
        alienSwarm.update()
        spaceship.update()
        projectiles.forEach { it.update() }
        projectiles.removeAll { it.y < 0 || it.used }

        var projectileIndex = 0
        while (projectileIndex < projectiles.size) {
            val projectile = projectiles[projectileIndex]
            if (!projectile.used) {
                var alienIndex = 0
                while (alienIndex < alienSwarm.aliens.size) {
                    val alien = alienSwarm.aliens[alienIndex]
                    if (alien.isCollidingWith(projectile)) {
                        projectile.used = true
                        alien.lives -= 1
                        if (alien.lives <= 0) {
                            alienSwarm.aliens.removeAt(alienIndex)
                            alienSwarm.createNewAlien() // Ajouter un nouvel alien à une position aléatoire
                            aliensKilled++
                        }
                        break
                    }
                    alienIndex++
                }
            }
            projectileIndex++
        }


        projectiles.removeAll { it.used }
    }

    fun pause() {
        thread.running = false
        thread.join()
    }

    fun resume() {
        if (!thread.running) {
            thread = GameThread(holder, this)
            thread.running = true
            thread.start()
        }
    }
}

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameBoard: GameBoard) :
    Thread() {
    @Volatile
    var running = false

    override fun run() {
        var canvas: Canvas?

        while (running) {
            canvas = null

            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameBoard.update()
                    gameBoard.draw(canvas)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
