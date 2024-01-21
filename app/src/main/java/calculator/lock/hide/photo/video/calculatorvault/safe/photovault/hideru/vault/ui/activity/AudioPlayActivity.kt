package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Toast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityAudioPlayBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver
import java.io.File

@AndroidEntryPoint
class AudioPlayActivity : BaseActivity<ActivityAudioPlayBinding>() {
    override fun getViewBinding() = ActivityAudioPlayBinding.inflate(layoutInflater)
    private val handler: Handler = Handler()
    private var mediaPlayer: MediaPlayer? = null

    override fun initData() {
        try {
            val path = intent.getStringExtra("audioPath") ?: return
            Log.e("TAG", "initData: ${path}")
            val file = File(path)
            mediaPlayer = MediaPlayer()
            bannerAd()


            mediaPlayer?.setDataSource(file.path)

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            mediaPlayer?.setAudioAttributes(audioAttributes)


            mediaPlayer?.prepare()
            mediaPlayer?.setOnPreparedListener { playerM -> playerM.start() }

            binding.artistName.isSelected = true
            binding.songname.isSelected = true
            binding.tvHeader.isSelected = true

            getFileInfo(path)

            binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress * 1000)
                    }
                    if (seekBar.progress >= seekBar.max) {
                        binding.playPause.setImageResource(R.drawable.img_exo_play)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

            runOnUiThread(object : Runnable {
                override fun run() {
                    if (isDestroyed) {
                        handler.removeCallbacks(this)
                    }
                    if (mediaPlayer != null && !isDestroyed) {
                        val mCurrentPosition = mediaPlayer?.currentPosition!! / 1000
                        binding.seekbar.progress = mCurrentPosition
                        binding.durationPlayed.text = formattedText(mCurrentPosition)
                        handler.postDelayed(this, 1000)
                    }
                }
            })

            binding.playPause.setOnClickListener(300L) {
                playPauseBtnClicked()
            }

            binding.ivBack.setOnClickListener(500L) {
                if (mediaPlayer != null && mediaPlayer?.isPlaying!!) {
                    playPauseBtnClicked()
                }
                onBackPressed()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.play_audio_error), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            Handler().postDelayed(Runnable {
                finish()
            }, 700)
            // Handle the exception (e.g., log the error)
        }


//        mediaPlayer?.start()
    }

    private fun getFileInfo(path: String) {
        val file = File(path)
        val duration = mediaPlayer?.duration!!
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        binding.seekbar.max = duration / 1000
        val formattedDuration = String.format("%d:%02d", minutes, seconds)
        binding.durationTotal.text = formattedDuration
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        binding.songname.text = file.name
        binding.tvHeader.text = file.name
        binding.artistName.text =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val art: ByteArray? = try {
            retriever.embeddedPicture ?: null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        } catch (e: RuntimeException) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }

        if (art != null) {
            binding.coverArt.setPadding(
                0,
                0,
                0,
                0
            )
            Glide.with(this)
                .load(art)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().addListener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        val paddingInPixels =
                            resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._120sdp)
                        binding.coverArt.setPadding(
                            paddingInPixels,
                            paddingInPixels,
                            paddingInPixels,
                            paddingInPixels
                        )
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.coverArt)
        } else {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.coverArt.setPadding(
                    0,
                    0,
                    0,
                    0
                )
            } else {
                val paddingInPixels = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp)
                binding.coverArt.setPadding(
                    paddingInPixels,
                    paddingInPixels,
                    paddingInPixels,
                    paddingInPixels
                )
            }
            binding.coverArt.setImageResource(R.drawable.ic_my_audio_ph)
        }
    }

    private fun formattedText(mCurrentPosition: Int): String? {
        var totalout = ""
        var totalNew = ""
        val seconds = (mCurrentPosition % 60).toString()
        val minutes = (mCurrentPosition / 60).toString()
        totalout = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"
        return if (seconds.length == 1) {
            totalNew
        } else {
            totalout
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    private fun playPauseBtnClicked() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying!!) {
            //pause the media
            binding.playPause.setImageResource(R.drawable.img_exo_play)
            mediaPlayer?.pause()
            binding.seekbar.max = mediaPlayer?.duration!! / 1000
            runOnUiThread(object : Runnable {
                override fun run() {
                    if (isDestroyed) {
                        handler.removeCallbacks(this)
                    }
                    if (mediaPlayer != null && !isDestroyed) {
                        val mCurrentPosition: Int =
                            mediaPlayer?.currentPosition!! / 1000
                        binding.seekbar.progress = mCurrentPosition
                        handler.postDelayed(this, 1000)
                    }
                }
            })
        } else if (mediaPlayer != null) {

            binding.playPause.setImageResource(R.drawable.img_exo_pause)
            mediaPlayer?.start()
            binding.seekbar.max = mediaPlayer?.duration!! / 1000
            runOnUiThread(object : Runnable {
                override fun run() {
                    if (isDestroyed) {
                        handler.removeCallbacks(this)
                    }
                    if (mediaPlayer != null && !isDestroyed) {
                        val mCurrentPosition: Int =
                            mediaPlayer?.currentPosition!! / 1000
                        binding.seekbar.progress = mCurrentPosition
                        handler.postDelayed(this, 1000)
                    }
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer != null && mediaPlayer?.isPlaying!!) {
            playPauseBtnClicked()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun networkStateChanged(state: NetworkChangeReceiver.NetworkState?) {
        super.networkStateChanged(state)
        if (state == NetworkChangeReceiver.NetworkState.CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).visible()
        } else if (state == NetworkChangeReceiver.NetworkState.NOT_CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).gone()
        }
    }
}