import android.graphics.Canvas
import android.view.SurfaceHolder
import com.altiso.finalblast.GameBoard

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
