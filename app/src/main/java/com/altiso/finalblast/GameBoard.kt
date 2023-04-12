package com.altiso.finalblast
import Alien
import GameThread
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
    private var spaceshipBitmap: Bitmap
    private var backgroundBitmap: Bitmap
    private val screenHeight: Int
    private val screenWidth: Int
    private val leaderboard = Leaderboard()
    private val projectiles = mutableListOf<Projectile>()
    private lateinit var spaceship: Spaceship
    private lateinit var alienSwarm: AlienSwarm
    private var shootTimer = 0L
    private val alienKilledObservers = mutableListOf<AlienKilledObserver>()
    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
        addAlienKilledObserver(leaderboard)
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

        spaceship = Spaceship(width, height, context)
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
            leaderboard.draw(canvas)

        }

        projectiles.removeAll(projectilesToRemove)
        alienSwarm.aliens.removeAll(aliensToRemove)
        projectilesToRemove.clear()
        aliensToRemove.clear()
    }

    private fun fireProjectile() {
        val projectile = spaceship.shoot()
        projectiles.add(projectile)
    }
    private fun addAlienKilledObserver(observer: AlienKilledObserver) {
        alienKilledObservers.add(observer)
    }
    private fun notifyAlienKilledObservers() {
        alienKilledObservers.forEach { it.onAlienKilled() }
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
                            notifyAlienKilledObservers() // Notifier les observateurs qu'un alien a été tué
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

