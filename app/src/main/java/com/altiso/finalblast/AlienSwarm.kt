import android.content.Context
import android.graphics.Canvas
import kotlin.random.Random

class AlienSwarm(context: Context, private val screenWidth: Int, private val screenHeight: Int) {
    private val aliens = mutableListOf<Alien>()

    init {
        val numAliens = 3

        for (i in 0 until numAliens) {
            val alien = Alien(context)
            alien.x = Random.nextFloat() * (screenWidth - alien.width)
            alien.y = Random.nextFloat() * (screenHeight / 2 - alien.height)
            aliens.add(alien)
        }
    }

    fun update() {
        aliens.forEach { alien ->
            val randomX = Random.nextFloat() * 10 - 5 // Déplacement aléatoire entre -5 et 5 sur l'axe des x
            val randomY = Random.nextFloat() * 10 - 5 // Déplacement aléatoire entre -5 et 5 sur l'axe des y

            alien.x += randomX
            alien.y += randomY

            // Empêcher les aliens de sortir de l'écran
            alien.x = alien.x.coerceIn(0f, (screenWidth - alien.width).toFloat())
            alien.y = alien.y.coerceIn(0f, (screenHeight / 2 - alien.height).toFloat())
        }
    }

    fun draw(canvas: Canvas) {
        aliens.forEach { it.draw(canvas) }
    }

     fun checkCollision(projectile: Projectile): Boolean {
        aliens.forEach { alien ->
            if (projectile.getProjectileX() + projectile.width >= alien.x && projectile.getProjectileX() <= alien.x + alien.width &&
                projectile.getProjectileY() + projectile.height >= alien.y && projectile.getProjectileY() <= alien.y + alien.height
            ) {
                return true
            }
        }
        return false
    }
}