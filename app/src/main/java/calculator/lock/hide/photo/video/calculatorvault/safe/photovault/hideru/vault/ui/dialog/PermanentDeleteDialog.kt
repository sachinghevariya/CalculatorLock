package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.DialogPermenatDeleteBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.DialogTrashMsgBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermanentDeleteDialog(
    private val callBack: (isDelete:Boolean) -> Unit
) : BaseDialog<DialogPermenatDeleteBinding>() {

    override fun getViewBinding() = DialogPermenatDeleteBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvTop.isSelected = true
        binding.btnCancel.isSelected = true
        binding.btnOk.isSelected = true
    }

    override fun init() {
        marqueEnable()
        binding.btnOk.setOnClickListener(1000L) {
            dismiss()
            callBack.invoke(true)
        }

        binding.btnCancel.setOnClickListener(1000L) {
            dismiss()
            callBack.invoke(false)
        }
    }

}