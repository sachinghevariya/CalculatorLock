package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemDocumentBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import java.io.File

class DocumentAdapter(
    var baseActivity: AppCompatActivity,
    var mediaItems: ArrayList<MediaItem>,
    var isFromHide: Boolean = false,
    var selectedList: ArrayList<MediaItem> = arrayListOf(),
    var isSelectionEnable: Boolean = false,
    private val functionCallback: (selectedList: ArrayList<MediaItem>, selectedItem: MediaItem?) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    fun setList(mediaItems: ArrayList<MediaItem>) {
        this.mediaItems = mediaItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemDocumentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mediaItems[position]
        holder.binding.tvName.isSelected = true
        holder.binding.cbSelected.isEnabled = false
        holder.binding.cbSelected.isChecked = checkIsItemSelected(item)
//        Glide.with(baseActivity).load(item.hidePath).centerCrop().sizeMultiplier(0.5f)
//            .into(holder.binding.ivMusic)
        val filename = item.hidePath.substringAfterLast("/")
        holder.binding.tvName.text = "$filename"
        if (!isSelectionEnable) {
            holder.binding.cbSelected.gone()
        } else {
            holder.binding.cbSelected.visible()
        }

        if (isSelectionEnable) {
            holder.binding.cbSelected.visible()
            holder.binding.root.setOnLongClickListener(null)
        } else {
            holder.binding.cbSelected.gone()
            holder.binding.root.setOnLongClickListener {
                // Enable selection mode
                isSelectionEnable = true
                if (selectedList.contains(item)) {
                    selectedList.remove(item)
                } else {
                    selectedList.add(item)
                }
                notifyItemChanged(position)
                functionCallback.invoke(selectedList, null)
                notifyDataSetChanged()
                true
            }
        }

        holder.binding.root.setOnClickListener(1000L) {
            if (isSelectionEnable) {
                // In selection mode, toggle the selection of the item
                if (selectedList.contains(item)) {
                    selectedList.remove(item)
                } else {
                    selectedList.add(item)
                }
                notifyItemChanged(position)
                functionCallback.invoke(selectedList, null)
            } else {
//                val intent = Intent(baseActivity, ImageViewerActivity::class.java)
//                CommonClass.pagerList.clear()
//                CommonClass.pagerList.addAll(mediaItems)
//                intent.putExtra("selectedItemPos", position)
//                baseActivity.startActivity(intent)
                if (isFromHide) {
                    val fileUri = FileProvider.getUriForFile(
                        baseActivity, "${baseActivity.packageName}.provider",
                        File(item.hidePath)
                    )

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(fileUri, "*/*")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    if (intent.resolveActivity(baseActivity.packageManager) != null) {
                        baseActivity.startActivity(intent)
                    } else {
                        baseActivity.showToast(baseActivity.getString(R.string.unable_to_open))
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mediaItems.size
    }

    private fun checkIsItemSelected(item: MediaItem): Boolean {
        return selectedList.contains(item)
    }
}