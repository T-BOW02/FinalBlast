import android.graphics.Canvas
import android.view.SurfaceHolder
import com.altiso.finalblast.GameBoard

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameBoard: GameBoard) : Thread() {
    private var running = false
    private val targetFPS = 60
    private val targetTime = (1000 / targetFPS).toLong()

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        var totalTime = 0L
        var frameCount = 0
        val targetTime = 1000 / targetFPS

        while (running) {
            startTime = System.nanoTime()
            var canvas: Canvas? = null

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

            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = targetTime - timeMillis

            try {
                sleep(waitTime.coerceAtLeast(0))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            totalTime += System.nanoTime() - startTime
            frameCount++

            if (frameCount == targetFPS) {
                frameCount = 0
                totalTime = 0
            }
        }
    }
}
