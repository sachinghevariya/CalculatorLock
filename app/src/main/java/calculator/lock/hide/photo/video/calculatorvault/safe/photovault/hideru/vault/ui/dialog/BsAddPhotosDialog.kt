package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import android.Manifest
import android.content.Intent
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentBsAddPhotosDialogBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.CameraActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.FileSelectionActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseBottomSheetFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager.FoldersActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.fragment.CameraFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BsAddPhotosDialog : BaseBottomSheetFragment<FragmentBsAddPhotosDialogBinding>() {

    override fun getViewBinding() = FragmentBsAddPhotosDialogBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvCamera.isSelected = true
        binding.tvPhotos.isSelected = true
        binding.tvFolder.isSelected = true
    }


    override fun init() {
        marqueEnable()
        binding.ivPhotos.setOnClickListener(1000L) {
            (requireActivity() as BaseActivity<*>).startActivity(Intent(requireActivity(), FileSelectionActivity::class.java).putExtra("isVideo",false))
            dismiss()
        }

        binding.ivCamera.setOnClickListener(1000L) {
            dismiss()
            (requireActivity() as BaseActivity<*>).startActivity(Intent(requireActivity(), CameraActivity::class.java))


        }

        binding.ivFolder.setOnClickListener(1000L) {
            (requireActivity() as BaseActivity<*>).startActivity(Intent(requireActivity(),FoldersActivity::class.java).putExtra("isVideo",false))
            dismiss()
        }
    }

}