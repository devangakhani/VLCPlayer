package com.playerdemo

import android.app.Activity
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import org.videolan.libvlc.IVLCVout
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.MediaPlayer
import java.util.*


class VideoController(activity: Activity): IVLCVout.Callback, MediaPlayer.EventListener {
    companion object {
       // private var logger= Logger.getLogger(VideoController::class.java)
    }

    private var mediaPlayer: MediaPlayer?=null
    var videoView: VideoView?=null
    private var videoWidth: Int = 0
    private var videoHeight: Int = 0
    private var libvlc: LibVLC?=null
    private var activity: Activity?=null
    init {
        this.activity=activity
    }


    /**
     * Used to set size for videoView

     * @param width
     * *
     * @param height
     */
    private fun setSize(width: Int, height: Int,screenWidth:Int,screenHeight:Int) {
        videoWidth = width
        videoHeight = height
        var w = screenWidth
        var h = screenHeight

        if (videoWidth * videoHeight <= 1)
            return

        if ( videoView == null)
            return

        val isPortrait =true /*resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT*/

        if (w > h && isPortrait || w < h && !isPortrait) {
            val i = w
            w = h
            h = i
        }

        val videoAR = videoWidth.toFloat() / videoHeight.toFloat()
        val screenAR = w.toFloat() / h.toFloat()

        if (screenAR < videoAR)
            h = (w / videoAR).toInt()
        else
            w = (h * videoAR).toInt()

        videoView!!.holder!!.setFixedSize(videoWidth, videoHeight)
        val lp = videoView!!.getLayoutParams()
        lp.width = w
        lp.height = h
        videoView!!.setLayoutParams(lp)
        videoView!!.invalidate()
    }

    /**
     * Creates MediaPlayer and plays video

     * @param media
     */
    fun createPlayer(media: String) {
        releasePlayer()
        try {
            if (media.length > 0){
               // logger.info(media)
            }

            // Create LibVLC
            val options = ArrayList<String>()
            // options.add("--subsdec-encoding <encoding>")
            options.add("--aout=opensles")
            options.add("--audio-time-stretch") // time stretching
            options.add("-vvv") // verbosity
            libvlc = LibVLC(activity,options)

            // Creating media player
            mediaPlayer = MediaPlayer(libvlc)
            mediaPlayer!!.setEventListener(this)

            // Seting up video output
            if(videoView!=null){
                videoView!!.setMediaController(MediaController(activity))
                Log.i("url = ",media)
                videoView!!.setVideoPath(media)
//              videoView!!.setVideoURI(Umedia))

                videoView!!.setOnPreparedListener(android.media.MediaPlayer.OnPreparedListener {
                    Log.d("TAG", "OnPrepared called") })
                videoView!!.start()
            }else{
                Log.i("error = ","null video view")
            }

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in creating player!", Toast
                    .LENGTH_LONG).show()
        }

    }

    fun releasePlayer() {
        if (libvlc == null)
            return
        mediaPlayer!!.stop()
        var vout: IVLCVout = mediaPlayer!!.vlcVout
        vout.removeCallback(this)
        vout.detachViews()
        videoView = null
        libvlc!!.release()
        libvlc = null;
        videoWidth = 0;
        videoHeight = 0;
    }

    override fun onEvent(event: MediaPlayer.Event) {

        when (event.type) {
            MediaPlayer.Event.EndReached -> {
                this.releasePlayer()
            }

            MediaPlayer.Event.Playing->Log.i("playing","playing")
            MediaPlayer.Event.Paused->Log.i("paused","paused")
            MediaPlayer.Event.Stopped->Log.i("stopped","stopped")
            else->Log.i("nothing","nothing")
        }
    }

    override fun onSurfacesCreated(vlcVout: IVLCVout?) {

    }

    override fun onSurfacesDestroyed(vlcVout: IVLCVout?) {

    }

    override fun onNewLayout(vlcVout: IVLCVout?, width: Int, height: Int, visibleWidth: Int, visibleHeight: Int, sarNum: Int, sarDen: Int) {
        if (width * height == 0)
            return;

        // store video size
        videoWidth = width
        videoHeight = height
        setSize(videoWidth, videoHeight,activity!!.window.decorView.width,activity!!.window.decorView.height)
    }


    override fun onHardwareAccelerationError(vlcVout: IVLCVout?) {
        this.releasePlayer()
    }

}