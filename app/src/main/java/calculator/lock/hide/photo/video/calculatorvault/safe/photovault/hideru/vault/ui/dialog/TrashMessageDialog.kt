package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.DialogTrashMsgBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrashMessageDialog(
    private val callBack: () -> Unit
) : BaseDialog<DialogTrashMsgBinding>() {

    override fun getViewBinding() = DialogTrashMsgBinding.inflate(layoutInflater)

    private fun marqueEnable() {
        binding.tvTop.isSelected = true
        binding.btnOk.isSelected = true
    }


    override fun init() {
        marqueEnable()
        binding.ivClose.setOnClickListener(1000L){
            dismiss()
        }
        binding.btnOk.setOnClickListener(1000L) {
            dismiss()
            callBack.invoke()
        }
    }

}