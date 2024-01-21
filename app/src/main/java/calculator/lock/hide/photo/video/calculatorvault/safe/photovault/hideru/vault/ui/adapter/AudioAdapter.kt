package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemAudioBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.AudioPlayActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import java.io.File

class AudioAdapter(
        var baseActivity: AppCompatActivity,
        var mediaItems: ArrayList<MediaItem>,
        var isFromHide: Boolean = false,
        var selectedList: ArrayList<MediaItem> = arrayListOf(),
        var isSelectionEnable: Boolean = false,
        private val functionCallback: (selectedList: ArrayList<MediaItem>, selectedItem: MediaItem?) -> Unit
) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {

    fun setList(mediaItems: ArrayList<MediaItem>) {
        this.mediaItems = mediaItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemAudioBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mediaItems[position]
        holder.binding.tvName.isSelected = true
        holder.binding.cbSelected.isEnabled = false
        holder.binding.cbSelected.isChecked = checkIsItemSelected(item)
//        Glide.with(baseActivity).load(item.hidePath).centerCrop().sizeMultiplier(0.5f)
//            .into(holder.binding.ivMusic)
        if(isFromHide) {
            val filename = item.hidePath.substringAfterLast("/")
            holder.binding.tvName.text = "$filename"
        }else {
            val filename = item.path.substringAfterLast("/")
            holder.binding.tvName.text = "$filename"
        }
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
                if(isFromHide) {

                    baseActivity.startActivity(Intent(baseActivity,AudioPlayActivity::class.java).putExtra("audioPath",item.hidePath))

//                    val audioFileUri = FileProvider.getUriForFile(baseActivity,"${baseActivity.packageName}.provider", File(item.hidePath))

//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.setDataAndType(audioFileUri, "audio/*")
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

//                    if (intent.resolveActivity(baseActivity.packageManager) != null) {
//                        baseActivity.startActivity(intent)
//                    } else {
//                        baseActivity.startActivity(Intent(baseActivity,AudioPlayActivity::class.java).putExtra("audioPath",item.hidePath))
//                        baseActivity.showToast(baseActivity.getString(R.string.unable_to_play))
//                    }



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