import android.content.Context
import android.graphics.Canvas
import com.altiso.finalblast.R

class Projectile(context: Context, startX: Float, startY: Float, screenWidth: Int, screenHeight: Int,) : GameObject(context) {
    private val moveSpeed = 15f
    var used: Boolean = false
    init {
        setBitmap(R.drawable.projectile, 10, 10) // Remplacez R.drawable.projectile par l'ID de la ressource de votre projectile
        x = startX - width / 2
        y = startY
    }
    fun getProjectileX(): Float {
        return x
    }

    fun getProjectileY(): Float {
        return y
    }
    fun isOutOfBounds(screenHeight: Int): Boolean {
        return y < 0 || y > screenHeight
    }

    override fun update() {
        y -= moveSpeed
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }
}
