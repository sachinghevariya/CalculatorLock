package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.DialogSecurityConfirmBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmSecurityDialog(
        private val question: String,
        private val answer: String,
        private val callBack: () -> Unit
) : BaseDialog<DialogSecurityConfirmBinding>() {

    override fun getViewBinding() = DialogSecurityConfirmBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvTop.isSelected = true
        binding.btnCancel.isSelected = true
        binding.btnOk.isSelected = true
        binding.tvHeaderQ.isSelected = true
        binding.tvQuestion.isSelected = true
        binding.tvHeaderA.isSelected = true
        binding.etAnswer.isSelected = true
        binding.tvTips.isSelected = true
    }


    override fun init() {
        marqueEnable()
        binding.tvQuestion.text = question
        binding.etAnswer.text = answer

        binding.btnOk.setOnClickListener(1000L) {
            dismiss()
            callBack.invoke()
        }

        binding.btnCancel.setOnClickListener(1000L) {
            dismiss()
        }
    }

}