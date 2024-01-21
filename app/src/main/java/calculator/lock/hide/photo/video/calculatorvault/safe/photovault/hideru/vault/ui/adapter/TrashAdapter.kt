package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemPhotosTrashBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.TrashImageViewerActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.CommonClass
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.getCurrentTimeInAMPM
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrashAdapter(
    var baseActivity: AppCompatActivity,
    var mediaItems: ArrayList<Any>,
    var selectedList: ArrayList<Any> = arrayListOf(),
    var isSelectionEnable: Boolean = false,
    var isVideo: Boolean = false,
    private val functionCallback: (selectedList: ArrayList<Any>, selectedItem: Any?) -> Unit
) : RecyclerView.Adapter<TrashAdapter.ViewHolder>() {

    fun setList(mediaItems: ArrayList<Any>) {
        this.mediaItems = mediaItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemPhotosTrashBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPhotosTrashBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mediaItems[position]
        holder.binding.cbSelected.isEnabled = false
        holder.binding.cbSelected.isChecked = checkIsItemSelected(item)
        holder.binding.tvDays.isSelected = true
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
        if (item is MediaItem) {
            holder.binding.notesCardContainer.gone()
            holder.binding.ivThumb.visible()
            if (item.type == MediaType.AUDIO) {
                Glide.with(baseActivity).load(R.drawable.ic_my_audio_ph).centerCrop()
                    .sizeMultiplier(0.5f)
                    .into(holder.binding.ivThumb)
            } else {
                Glide.with(baseActivity).load(item.hidePath).centerCrop().sizeMultiplier(0.5f)
                    .into(holder.binding.ivThumb)
            }
            setRemainingDays(holder.binding.tvDays, item.deleteDate)
        } else if (item is NoteItem) {
            holder.binding.ivThumb.gone()
            holder.binding.notesCardContainer.visible()
            holder.binding.tvDateAdded.isSelected = true
            holder.binding.tvTitle.isSelected = true
            holder.binding.tvTitle.text = "${item.noteTitle}"
//            holder.binding.tvDescription.text = "${item.noteDescription}"
            holder.binding.tvDateAdded.text =
                "${baseActivity.getString(R.string.date)} ${getCurrentTimeInAMPM(item.createdDate)}"
            setRemainingDays(holder.binding.tvDays, item.deleteDate)
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
                if (item is MediaItem) {
                    val onlyMediaItems =
                        mediaItems.filterIsInstance<MediaItem>() as ArrayList<MediaItem>
                    val newItems = onlyMediaItems.filter { it.type != MediaType.AUDIO }
                    CommonClass.pagerList.clear()
                    CommonClass.pagerList.addAll(newItems)
                    if (newItems != null && newItems.isNotEmpty()) {
                        if(item.type != MediaType.AUDIO) {
                            val intent = Intent(baseActivity, TrashImageViewerActivity::class.java)
                            intent.putExtra("selectedItemPos", position)
                            intent.putExtra("isVideo", isVideo)
                            baseActivity.startActivity(intent)
                        }
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return mediaItems.size
    }

    private fun checkIsItemSelected(item: Any): Boolean {
        return selectedList.contains(item)
    }

    private fun setRemainingDays(viewText: TextView, deleteDate: String) {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        CoroutineScope(Dispatchers.IO).launch {
            if (deleteDate.isNotEmpty()) {
                val deleteDate = dateFormat.parse(deleteDate)
                val daysDifference = daysBetweenDates(deleteDate, currentDate)
                withContext(Dispatchers.Main) {
                    val remainDays = 15 - daysDifference
                    viewText.text = "$remainDays ${baseActivity.getString(R.string.days)}"
                }
            }

        }
    }

    private fun daysBetweenDates(startDate: Date, endDate: Date): Long {
        val difference = endDate.time - startDate.time
        return difference / (1000 * 60 * 60 * 24)
    }
}