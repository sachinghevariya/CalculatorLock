package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemFolderNameBinding

class FolderNameListAdapter(
    var folderItems: List<String>,
    private val functionCallback: (folderName: String) -> Unit
) : RecyclerView.Adapter<FolderNameListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemFolderNameBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFolderNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = folderItems[position]
        holder.binding.tvFolderName.text = "$item"
        holder.binding.root.setOnClickListener(1000L) {
            functionCallback.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return folderItems.size
    }

}