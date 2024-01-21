package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemNoteBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.TrashImageViewerActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.CommonClass
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.getCurrentTimeInAMPM
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesAdapter(
    var baseActivity: AppCompatActivity,
    var noteItems: ArrayList<NoteItem>,
    var isSelectionEnable: Boolean = false,
    var selectedList: ArrayList<NoteItem> = arrayListOf(),
    private val functionCallback: (selectedList: ArrayList<NoteItem>,noteIem:NoteItem?) -> Unit,
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    fun setList(noteItems: ArrayList<NoteItem>) {
        this.noteItems = noteItems
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = noteItems[position]
        holder.binding.cbSelected.isEnabled = false
        holder.binding.cbSelected.isChecked = checkIsItemSelected(item)
        holder.binding.tvTitle.isSelected = true
        holder.binding.tvDateAdded.isSelected = true
        holder.binding.tvTitle.text = "${item.noteTitle}"
//        holder.binding.tvDescription.text = "${item.noteDescription}"
        holder.binding.tvDateAdded.text = "${baseActivity.getString(R.string.date)} ${getCurrentTimeInAMPM(item.createdDate)}"
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


        holder.itemView.setOnClickListener(1000L){
            if (isSelectionEnable) {
                if (selectedList.contains(item)) {
                    selectedList.remove(item)
                } else {
                    selectedList.add(item)
                }
                notifyItemChanged(position)
                functionCallback.invoke(selectedList,null)
            } else {
                selectedList.clear()
                functionCallback.invoke(selectedList, item)
            }
        }

    }


    override fun getItemCount(): Int {
        return noteItems.size
    }

    private fun checkIsItemSelected(item: NoteItem): Boolean {
        return selectedList.contains(item)
    }

}