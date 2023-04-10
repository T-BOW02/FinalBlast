package com.altiso.finalblast
import android.view.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameBoard: GameBoard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        gameBoard = GameBoard(this)

        setContentView(gameBoard)


    }

    override fun onPause() {
        super.onPause()
        gameBoard.pause()
    }

    override fun onResume() {
        super.onResume()
        gameBoard.resume()
    }
}