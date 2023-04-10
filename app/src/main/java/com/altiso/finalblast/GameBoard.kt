import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.util.DisplayMetrics
import android.view.WindowManager
class GameBoard(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    val thread: GameThread
    private val spaceship: Spaceship
    private val alienSwarm: AlienSwarm
    private val projectiles = mutableListOf<Projectile>()
//    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)

//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val screenWidth = displayMetrics.widthPixels
//        val screenHeight = displayMetrics.heightPixels
        spaceship = Spaceship( width / 2f, height - 200f, 200, 200,context)
        alienSwarm = AlienSwarm(context)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread.setRunning(true)
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.setRunning(false)
                thread.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val projectile = spaceship.shoot()
            projectiles.add(projectile)
        }
        return true
    }
    private fun updateProjectiles() {
        val iterator = projectiles.iterator()
        while (iterator.hasNext()) {
            val projectile = iterator.next()
            projectile.update()
            if (alienSwarm.checkCollision(projectile)) {
                iterator.remove()
            } else if (projectile.y < 0) {
                iterator.remove()
            }
        }}
    fun update() {
        spaceship.update()
        alienSwarm.update()
        projectiles.forEach { it.update() }
        projectiles.removeAll { it.isOutOfBounds(height) || alienSwarm.checkCollision(it) }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        spaceship.draw(canvas)
        alienSwarm.draw(canvas)
        projectiles.forEach { it.draw(canvas) }
    }
}
