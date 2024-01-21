package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentImageViewerBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class PagerAdapter(
    private val transitionName: String,
    private var imageUrlList: ArrayList<MediaItem>,
    var isVideo: Boolean,
    private val functionCallBack: (mediaItem: MediaItem, isShowMenu: Boolean) -> Unit
) : RecyclerView.Adapter<PagerAdapter.ViewPagerViewHolder>() {

    private var isShowMenu = false
    private var isPlaying = false
    private var currentPlayingPosition: Int? = null

    fun setList(mediaItems: ArrayList<MediaItem>, position: Int) {
        this.imageUrlList = mediaItems

        if (isVideo) {
            isPlaying = position == currentPlayingPosition
        }
        notifyDataSetChanged()
    }

    inner class ViewPagerViewHolder(val binding: FragmentImageViewerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(mediaItem: MediaItem, position: Int) {
            isVideo = mediaItem.type == MediaType.VIDEO

            if (isVideo) {
                binding.videoThumbnail.visible()
                binding.ivPlayPause.setImageResource(R.drawable.ic_play)
                functionCallBack.invoke(mediaItem, true)
                binding.containerVideo.visible()
                binding.videoView.gone()
                binding.videoThumbnail.visible()
                binding.ivPlayPause.visible()
                binding.ivPhoto.gone()
                playVideo(binding.videoView, mediaItem, binding.videoThumbnail)
                binding.videoView.setOnCompletionListener {
                    binding.videoView.gone()
                    binding.containerVideo.visible()
                    binding.ivPlayPause.visible()
                    binding.ivPhoto.gone()
                    binding.videoThumbnail.visible()
                    binding.ivPlayPause.setImageResource(R.drawable.ic_play)
                    Glide.with(binding.videoThumbnail.context).load(mediaItem.hidePath)
                        .into(binding.videoThumbnail)
                    isPlaying = false
                    currentPlayingPosition = null
                }
                isPlaying = position == currentPlayingPosition
            } else {
                binding.containerVideo.gone()
                binding.videoView.gone()
                binding.ivPlayPause.gone()
                binding.ivPhoto.visible()
                Glide.with(binding.root.context)
                    .load(mediaItem.hidePath)
                    .error(R.mipmap.ic_launcher)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivPhoto)
            }

            binding.root.setOnClickListener {
                isShowMenu = !isShowMenu
                if (isVideo) {
                    binding.videoThumbnail.gone()
                    binding.videoView.visible()
                    if (isPlaying) {
                        binding.ivPlayPause.setImageResource(R.drawable.ic_play)
                        binding.videoView.pause()
                        currentPlayingPosition = null
                    } else {
                        binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
                        binding.videoView.start()
                        currentPlayingPosition = position

                    }
                    isPlaying = !isPlaying
                    functionCallBack.invoke(mediaItem, true)
                } else {
                    functionCallBack.invoke(mediaItem, true)
                }
            }
        }
    }

    override fun getItemCount(): Int = imageUrlList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding = FragmentImageViewerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.setData(imageUrlList[position], position)
    }

    fun playVideo(videoView: VideoView, mediaItem: MediaItem, videoThumbnailImageView: ImageView) {
        val videoPath = mediaItem.hidePath
        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.pause()
        Glide.with(videoThumbnailImageView.context).load(videoPath).into(videoThumbnailImageView)
    }

    fun pauseVideo(position: Int) {
        if (isVideo && isPlaying && currentPlayingPosition == position) {
            isPlaying = false
            currentPlayingPosition = null
            notifyItemChanged(position)
        }
        if (isVideo && isPlaying && currentPlayingPosition != position) {
            currentPlayingPosition?.let {
                notifyItemChanged(it)
            }
            isPlaying = false
            currentPlayingPosition = null
        }

        if (isVideo && !isPlaying){
            notifyItemChanged(position)
        }
    }

    fun pauseVideo(isVideo: Boolean, position: Int) {
        this.isVideo = isVideo
        if (isVideo && isPlaying && currentPlayingPosition != position) {
            currentPlayingPosition?.let {
                notifyItemChanged(it)
            }
            isPlaying = false
            currentPlayingPosition = null
        }
    }

    fun stopVideo(position: Int){
        isPlaying = false
        currentPlayingPosition = null
        try {
            notifyItemChanged(position)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

}