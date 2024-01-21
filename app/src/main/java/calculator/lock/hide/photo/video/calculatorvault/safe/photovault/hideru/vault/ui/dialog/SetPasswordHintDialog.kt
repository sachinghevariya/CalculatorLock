package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.DialogSetPassBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetPasswordHintDialog : BaseBottomSheetFragment<DialogSetPassBinding>() {

    override fun getViewBinding() = DialogSetPassBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvSetPass.isSelected = true
        binding.tvConfirm.isSelected = true
    }

    override fun init() {
        marqueEnable()
        binding.btnConfirm.setOnClickListener(1000L) {
            dismiss()
        }
    }

}