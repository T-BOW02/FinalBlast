import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import com.altiso.finalblast.R

class Alien(context: Context) : GameObject(context) {
    private val moveSpeed = 3f

    init {
        setBitmap(R.drawable.alien, 60, 40) // Remplacez R.drawable.alien par l'ID de la ressource de votre alien
    }

    fun isCollidingWith(projectile: Projectile): Boolean {
        val alienRect = RectF(x, y, x + width, y + height)
        val projectileRect = RectF(projectile.x, projectile.y, projectile.x + projectile.width, projectile.y + projectile.height)
        return alienRect.intersect(projectileRect)
    }

    override fun update() {
        x += moveSpeed
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }
}