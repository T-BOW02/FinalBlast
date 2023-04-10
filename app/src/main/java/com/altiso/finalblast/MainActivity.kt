package com.altiso.finalblast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var gameBoard: GameBoard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameBoard = GameBoard(this)

        setContentView(R.layout.activity_main)


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