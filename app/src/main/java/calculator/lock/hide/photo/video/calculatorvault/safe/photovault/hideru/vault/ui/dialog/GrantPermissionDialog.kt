package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.DialogGrantPermissionBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GrantPermissionDialog(private val functionCallback:()->Unit) : BaseDialog<DialogGrantPermissionBinding>() {

    override fun getViewBinding() = DialogGrantPermissionBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvTop.isSelected = true
        binding.btnCancel.isSelected = true
        binding.btnOk.isSelected = true
    }

    override fun init() {
        marqueEnable()
        binding.btnCancel.setOnClickListener(1000L) {
            dismissSafely()
        }
        binding.btnOk.setOnClickListener(1000L) {
            functionCallback.invoke()
            dismissSafely()
        }
    }

    private fun dismissSafely() {
        val fragmentManager = parentFragmentManager
        if (!fragmentManager.isStateSaved) {
            dismiss()
        }
    }

}