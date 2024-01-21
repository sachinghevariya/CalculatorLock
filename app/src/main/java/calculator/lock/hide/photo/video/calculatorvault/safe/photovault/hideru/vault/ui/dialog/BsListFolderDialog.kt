package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.FragmentBsListFolderDialogBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.FolderNameListAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BsListFolderDialog(
    private val folderList: List<String>,
    private val functionCallback: (folderName: String) -> Unit
) : BaseDialog<FragmentBsListFolderDialogBinding>() {

    override fun getViewBinding() = FragmentBsListFolderDialogBinding.inflate(layoutInflater)

    override fun init() {
        binding.rvFolderNameList.layoutManager = LinearLayoutManager(requireActivity(),RecyclerView.VERTICAL,false)
        binding.rvFolderNameList.adapter = FolderNameListAdapter(folderList) {
            functionCallback.invoke(it)
            dismiss()
        }
    }

}