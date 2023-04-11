import android.content.Context
import android.graphics.Canvas
import com.altiso.finalblast.R

class Spaceship(x: Double, y: Double, private val screenWidth: Int, private val screenHeight: Int, context: Context) : GameObject(context) {
    private val moveSpeed = 25f


    init {
        setBitmap(R.drawable.spaceship, 50, 50) // Remplacez R.drawable.spaceship par l'ID de la ressource de votre vaisseau spatial

    }

    fun moveLeft() {
        x -= moveSpeed
        if (x < 0) {
            x = 0f
        }
    }

    fun moveRight() {
        x += moveSpeed
        if (x + width > screenWidth) {
            x = screenWidth - width.toFloat()
        }
    }

    fun shoot(): Projectile {
        return Projectile(context,x + width / 2, y, screenWidth, screenHeight)
    }

    override fun update() {
        // Ajoutez ici la logique de mise à jour spécifique au vaisseau spatial si nécessaire
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }
}