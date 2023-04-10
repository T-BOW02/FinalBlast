import android.content.Context
import android.graphics.Canvas
import com.altiso.finalblast.R

class Spaceship(x: Float, y: Float, private val screenWidth: Int,private val screenHeight: Int, context: Context) : GameObject(context) {
    private val moveSpeed = 10f

    init {
        setBitmap(R.drawable.spaceship, 100, 100) // Remplacez R.drawable.spaceship par l'ID de la ressource de votre vaisseau spatial

    }

    fun moveLeft() {
        x -= moveSpeed
    }

    fun moveRight() {
        x += moveSpeed
    }

    fun shoot(): Projectile {
        return Projectile(context,x + width / 2, y - height, screenWidth, screenHeight)
    }

    override fun update() {
        // Ajoutez ici la logique de mise à jour spécifique au vaisseau spatial si nécessaire
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }
}