package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment

import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentImageViewerBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import com.bumptech.glide.Glide

class ImageViewerFragment(private val mediaItems: ArrayList<MediaItem>) :
    BaseFragment<FragmentImageViewerBinding>() {

    override fun getViewBinding() = FragmentImageViewerBinding.inflate(layoutInflater)

    override fun init() {
        Glide.with(requireActivity())
            .load(mediaItems[0].hidePath)
            .error(R.mipmap.ic_launcher)
            .into(binding.ivPhoto)


//        binding.ivPhoto.setOnClickListener(400L) {
//            if (binding.buttonView.visibility == View.VISIBLE) {
//                binding.buttonView.inVisible()
//            } else {
//                binding.buttonView.visible()
//            }
//        }
    }

}