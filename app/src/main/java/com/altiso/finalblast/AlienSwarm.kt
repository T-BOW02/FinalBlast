import android.content.Context
import android.graphics.Canvas

class AlienSwarm(context: Context) {
    private val aliens = mutableListOf<Alien>()
    private val moveSpeed = 5f
    private val alienRows = 4
    private val alienColumns = 8

    init {
        val alienSpacing = 100f
        val startTop = 200f
        val startLeft = 100f
        for (i in 0 until alienRows) {
            for (j in 0 until alienColumns) {
                val alien = Alien(context)
                alien.x = startLeft + j * alienSpacing
                alien.y = startTop + i * alienSpacing
                aliens.add(alien)
            }
        }
    }

    fun update() {
        aliens.forEach { it.update() }
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