package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentBsAddFolderDialogBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BsAddFolderDialog(private val functionCallback: (folderName: String) -> Unit) :
        BaseDialog<FragmentBsAddFolderDialogBinding>() {

    private val viewModel: FileManagerViewModel by viewModels()

    override fun getViewBinding() = FragmentBsAddFolderDialogBinding.inflate(layoutInflater)

    private var folderName = ""

    private fun marqueEnable() {
        binding.tvTop.isSelected = true
        binding.btnCancel.isSelected = true
        binding.btnOk.isSelected = true
    }

    override fun init() {
        marqueEnable()
        binding.etFolderName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    folderName = s.toString().trim()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })

        binding.btnOk.setOnClickListener(1000L) {
            if (folderName.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.Main) {
                    val folderExists = viewModel.checkIsFolderExist(folderName)
                    if (folderExists) {
                        requireActivity().showToast(getString(R.string.folder_exist))
                    } else {
                        functionCallback.invoke(folderName)
                        dismiss()
                    }
                }
            } else {
                requireActivity().showToast(getString(R.string.toast_msg_add_folder))
            }
        }

        binding.btnCancel.setOnClickListener(1000L) {
            dismiss()
        }


    }

}