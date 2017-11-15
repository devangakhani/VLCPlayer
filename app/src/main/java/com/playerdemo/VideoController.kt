package com.playerdemo

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import org.videolan.libvlc.IVLCVout
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.util.*


class VideoController(activity: Activity): IVLCVout.Callback, MediaPlayer.EventListener {
    // create TAG for logging
    companion object {
        private var TAG= "VideoController"
    }
    // declare media player object
    private var mediaPlayer: MediaPlayer?=null
    // declare surface view object
    var mSurface: SurfaceView?=null
    // declare surface holder object
    var holder: SurfaceHolder?= null

    // declare libvlc object
    private var libvlc: LibVLC?=null

    // declare/initialize activity
    private var activity: Activity?=null
    init {
        this.activity=activity
    }




    /**
     * Creates MediaPlayer and plays video

     * @param media
     */
    fun createPlayer(media: String) {
        if(mediaPlayer!=null && libvlc!=null){
            releasePlayer()
        }
        Log.i(TAG, "Creating vlc player")
        try {
            // create arraylist to assign option to create libvlc object
            val options = ArrayList<String>()
            options.add("--aout=opensles")
            options.add("--http-reconnect")
            options.add("--audio-time-stretch") // time stretching
            options.add("--network-caching=1500")
            options.add("-vvv") // verbosity

            // create libvlc object
            libvlc = LibVLC(activity, options)

            // get surface view holder to display video
            this.holder=mSurface!!.holder
            holder!!.setKeepScreenOn(true)

            // Creating media player
            mediaPlayer = MediaPlayer(libvlc)

            // Setting up video output
            val vout = mediaPlayer!!.vlcVout
            vout.setVideoView(mSurface)
            vout.addCallback(this)
            vout.attachViews()
            val m = Media(libvlc, Uri.parse(media))
            mediaPlayer!!.setMedia(m)
            mediaPlayer!!.play()


        } catch (e: Exception) {
            Toast.makeText(activity, "Error in creating player!", Toast
                    .LENGTH_LONG).show()
        }

    }

    /*
   * release player
   * */
    fun releasePlayer() {
        Log.i(TAG,"releasing player started")
        if (libvlc == null)
            return
        mediaPlayer!!.stop()
        var vout: IVLCVout = mediaPlayer!!.vlcVout
        vout.removeCallback(this)
        vout.detachViews()
        mediaPlayer!!.release()
        mediaPlayer=null
        holder = null
        libvlc!!.release()
        libvlc = null

        Log.i(TAG,"released player")
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
        val sw = mSurface!!.width
        val sh = mSurface!!.height

        if (sw * sh == 0) {
            Log.e(TAG, "Invalid surface size")
            return
        }

        mediaPlayer!!.vlcVout.setWindowSize(sw, sh)
        mediaPlayer!!.aspectRatio="4:3"
        mediaPlayer!!.setScale(0f)
    }

    override fun onSurfacesDestroyed(vlcVout: IVLCVout?) {
        releasePlayer()
    }

}