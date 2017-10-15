package com.playerdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var videoView: VideoView? = null

    private var videoController: VideoController?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
        videoView = videoView1
        var etUrl=etURL
        etUrl.setText("https://d1pmarobgdhgjx.cloudfront.net/education/ED_pause-and-think-online.mp4")
        var btn=btnOk
        videoController= VideoController(this)
        videoController!!.videoView=videoView
        btn.setOnClickListener(View.OnClickListener {
            videoController!!.createPlayer(etUrl.text.toString())
        })
    }

}

