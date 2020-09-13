package com.mahmoud.buffup

import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoPath("https://buffup-public.s3.eu-west-2.amazonaws.com/video/toronto+nba+cut+3.mp4")
        videoView.start()
    }

}