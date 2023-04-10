package com.altiso.finalblast
import android.view.MotionEvent
import Spaceship
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.view.WindowManager

@SuppressLint("ClickableViewAccessibility")
class GameBoard(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var thread: GameThread
    private lateinit var spaceshipBitmap: Bitmap
    private lateinit var backgroundBitmap: Bitmap
    private val screenHeight: Int
    private val screenWidth: Int

    private lateinit var spaceship: Spaceship

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
        // Initialisation et redimensionnement de l'image d'arrière-plan
        backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.background)
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, true)
        // Initialisation du vaisseau spatial

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

        // Dessinez l'arrière-plan
        canvas.drawBitmap(backgroundBitmap, 0f, 0f, paint)

        // Dessinez le vaisseau spatial
        spaceship.draw(canvas)

    }

    fun update() {
        // Mettez à jour l'état de votre jeu ici
        spaceship.update()
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
