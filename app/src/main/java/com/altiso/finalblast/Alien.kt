import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import com.altiso.finalblast.R
import kotlin.random.Random

class Alien(context: Context) : GameObject(context) {
    private val moveSpeed = 10f
    private var direction = 1
    private var moveDirection = Random.nextInt(200, 600) // Pixels à parcourir dans une direction avant de changer de direction
    private var movedPixels = 0
    private var delayCounter = 0
    private val delayBetweenDirectionChanges = 30
    init {
        setBitmap(R.drawable.alien, 60, 40) // Remplacez R.drawable.alien par l'ID de la ressource de votre alien
    }

    fun isCollidingWith(projectile: Projectile): Boolean {
        val alienRect = RectF(x, y, x + width, y + height)
        val projectileRect = RectF(projectile.x, projectile.y, projectile.x + projectile.width, projectile.y + projectile.height)
        return alienRect.intersect(projectileRect)
    }

    override fun update() {
        if (delayCounter < delayBetweenDirectionChanges) {
            delayCounter++
        } else {
            if (movedPixels >= moveDirection) {
                direction *= -1
                moveDirection = Random.nextInt(200, 600)
                movedPixels = 0
                delayCounter = 0
            }

            x += moveSpeed * direction
            movedPixels += (moveSpeed * direction).toInt()
        }
    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }
}