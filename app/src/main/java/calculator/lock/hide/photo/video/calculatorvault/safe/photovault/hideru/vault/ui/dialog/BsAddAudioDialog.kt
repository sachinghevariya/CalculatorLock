package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import android.content.Intent
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentBsAddAudioDialogBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.FileSelectionActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseBottomSheetFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager.FoldersActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BsAddAudioDialog : BaseBottomSheetFragment<FragmentBsAddAudioDialogBinding>() {

    override fun getViewBinding() = FragmentBsAddAudioDialogBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvAudios.isSelected = true
        binding.tvFolder.isSelected = true
    }


    override fun init() {
        marqueEnable()
        binding.ivAuido.setOnClickListener(1000L) {
            (requireActivity() as BaseActivity<*>).startActivity(Intent(requireActivity(), FileSelectionActivity::class.java).putExtra("isAudio",true))
            dismiss()
        }

        binding.ivFolder.setOnClickListener(1000L) {
            (requireActivity() as BaseActivity<*>).startActivity(Intent(requireActivity(),FoldersActivity::class.java).putExtra("isAudio",true).putExtra("isVideo",false))
            dismiss()
        }
    }

}