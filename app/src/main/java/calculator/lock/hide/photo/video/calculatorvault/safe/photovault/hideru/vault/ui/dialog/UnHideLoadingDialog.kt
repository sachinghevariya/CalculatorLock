package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieListener
import com.airbnb.lottie.OnCompositionLoadedListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentHideLoadingDialogBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.moveMediaFileUsingCursor
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class UnHideLoadingDialog(
    val selectedList: ArrayList<MediaItem>,
    private val callBack: () -> Unit
) : BaseDialog<FragmentHideLoadingDialogBinding>() {

    private val viewModel: FileManagerViewModel by viewModels()

    override fun getViewBinding() = FragmentHideLoadingDialogBinding.inflate(layoutInflater)
    private fun marqueEnable() {
        binding.tvTop.isSelected = true
        binding.btnOk.isSelected = true
    }

    override fun init() {
        marqueEnable()
        binding.tvMsg.text = "${getString(R.string.msg_rm_media_from_vault)}"
        Glide.with(requireActivity()).load(R.drawable.ic_loading_unlock).into(binding.ivIcon)
        binding.btnOk.isEnabled = false
        CoroutineScope(Dispatchers.IO).launch {
            val selectedListCopy = ArrayList(selectedList)
            val deferredList = selectedListCopy.map { mediaItem ->
                async(Dispatchers.IO) {
                    moveMediaFileUsingCursor(requireActivity(), mediaItem) { hidePath ->
                        viewModel.removeMediaItemToDb(mediaItem)
                    }
                }
            }


            // Wait for all async tasks to complete
            deferredList.awaitAll()
            withContext(Dispatchers.Main){
                binding.btnOk.isEnabled = true
                binding.tvTop.text = "${getString(R.string.done)}"
                val rawResId = R.raw.done_file
                val animationJson = resources.openRawResource(rawResId).bufferedReader().use { it.readText() }

                LottieComposition.Factory.fromJsonString(animationJson, object : LottieListener<LottieComposition>,
                    OnCompositionLoadedListener {
                    override fun onResult(composition: LottieComposition?) {

                    }

                    override fun onCompositionLoaded(composition: LottieComposition?) {
                        if (composition != null) {
                            binding.ivIcon.setComposition(composition)
                            binding.ivIcon.playAnimation()
                        }
                    }
                })            }
            // Code here will execute after all tasks are done

        }

        binding.btnOk.setOnClickListener(1000L) {
            dismiss()
            callBack.invoke()
        }
    }

}