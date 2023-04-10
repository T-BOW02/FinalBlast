import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.altiso.finalblast.R
class MainActivity : AppCompatActivity() {
    private lateinit var gameBoard: GameBoard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startGameButton = findViewById<Button>(R.id.start_game_button)
        val gameContainer = findViewById<FrameLayout>(R.id.game_container)

        startGameButton.setOnClickListener {
            gameBoard = GameBoard(this)
            gameContainer.addView(gameBoard)
            startGameButton.isEnabled = false // Désactivez le bouton pour éviter de créer plusieurs instances du jeu
        }
    }

    override fun onPause() {
        super.onPause()
        gameBoard.thread.setRunning(false)
    }

    override fun onResume() {
        super.onResume()
        if (::gameBoard.isInitialized) {
            gameBoard.thread.setRunning(true)
        }
    }
}