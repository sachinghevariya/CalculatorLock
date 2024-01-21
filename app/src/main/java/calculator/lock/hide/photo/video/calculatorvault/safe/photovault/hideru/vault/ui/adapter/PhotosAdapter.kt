package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemPhotosBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.ImageViewerActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.CommonClass
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import com.bumptech.glide.Glide

class PhotosAdapter(
    var baseActivity: AppCompatActivity,
    var mediaItems: ArrayList<MediaItem>,
    var selectedList: ArrayList<MediaItem> = arrayListOf(),
    var isHidePhoto: Boolean = false,
    var isSelectionEnable: Boolean = false,
    var isVideo: Boolean = false,
    private val functionCallback: (selectedList: ArrayList<MediaItem>, selectedItem: MediaItem?) -> Unit
) : RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

    fun setList(mediaItems: ArrayList<MediaItem>) {
        this.mediaItems = mediaItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemPhotosBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhotosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mediaItems[position]
        holder.binding.cbSelected.isEnabled = false
        holder.binding.cbSelected.isChecked = checkIsItemSelected(item)
        if (isHidePhoto) {
            Glide.with(baseActivity).load(item.hidePath).centerCrop().sizeMultiplier(0.5f)
                .into(holder.binding.ivThumb)

            if (!isSelectionEnable) {
                holder.binding.cbSelected.gone()
            } else {
                holder.binding.cbSelected.visible()
            }

            if (isSelectionEnable) {
                holder.binding.cbSelected.visible()
                holder.binding.root.setOnLongClickListener(null) // Disable long click listener
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

        } else {
            holder.binding.cbSelected.visible()
            Glide.with(baseActivity).load(item.path).centerCrop().sizeMultiplier(0.5f)
                .into(holder.binding.ivThumb)
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
//                functionCallback.invoke(arrayListOf(),item)
                CommonClass.pagerList.clear()
                CommonClass.pagerList.addAll(mediaItems)
                if (CommonClass.pagerList.isNotEmpty()) {
                    val intent = Intent(baseActivity, ImageViewerActivity::class.java)
                    intent.putExtra("selectedItemPos", position)
                    intent.putExtra("isVideo", isVideo)
                    baseActivity.startActivity(intent)
                }
                // Handle regular item click (e.g., open item details)
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