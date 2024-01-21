package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import android.content.Intent
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentBsAddVideosDialogBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.FileSelectionActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseBottomSheetFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager.FoldersActivity

class BsAddVideosDialog : BaseBottomSheetFragment<FragmentBsAddVideosDialogBinding>() {

    override fun getViewBinding() = FragmentBsAddVideosDialogBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvVideos.isSelected = true
        binding.tvFolder.isSelected = true
    }

    override fun init() {
        marqueEnable()
        binding.ivVideos.setOnClickListener(1000L) {
            startActivity(Intent(requireActivity(), FileSelectionActivity::class.java).putExtra("isVideo",true))
            dismiss()
        }


        binding.ivFolder.setOnClickListener(1000L) {
            startActivity(Intent(requireActivity(), FoldersActivity::class.java).putExtra("isVideo",true))
            dismiss()
        }
    }

}