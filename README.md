# VLCPlayer
VLC Player integration using Kotlin

#  Installation
Add below dependancy in application build.gradle
```gradle
dependencies {
    // it is suppporting ABIs likes armeabi-v7a, arm64-v8a, x86 and x86_64.
    compile 'de.mrmaffen:libvlc-android:2.1.12@aar'    
}
```
Also include the following required permission in your manifest.
```xml
<!--if you want to play server URL-->
<uses-permission android:name="android.permission.INTERNET" />
```
Implements IVLCVout.Callback and MediaPlayer.EventListener in activity or fragment

Include following veriables at class level
```kotlin
// declare media player object
private var mediaPlayer: MediaPlayer?=null
// declare surface view object
var mSurface: SurfaceView?=null
// declare surface holder object
var holder: SurfaceHolder?= null
// declare libvlc object
private var libvlc: LibVLC?=null
```
Add following method to create player to play provided URL
```kotlin
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
    Toast.makeText(activity, "Error in creating player!", Toast.LENGTH_LONG).show()
  }

}
```
Add following method to release player
```kotlin
/**
* release player
*/
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
```
Add below code snippet in OnEvent override method of MediaPlayer.EventListener
```kotlin
override fun onEvent(event: MediaPlayer.Event) {
  when (event.type) {
  MediaPlayer.Event.EndReached -> {
    this.releasePlayer()
  }            
  else->Log.i(TAG,"nothing")
  }
}
```
Add below code snippet in onSurfacesCreated override method of IVLCVout.Callback
```kotlin
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
```
