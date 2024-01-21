package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemAlbumsBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemAlbumsDialogBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity.AlbumActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.inVisible
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import com.bumptech.glide.Glide

class AlbumsAdapter(
    var baseActivity: AppCompatActivity,
    var folderItems: MutableList<FolderItemWithMediaItems>,
    var isFromBottomSheet: Boolean = false,
    var isVideo: Boolean = false,
    var isAudio: Boolean = false,
    var isDoc: Boolean = false,
    var isSelectionEnable: Boolean = false,
    var selectedList: ArrayList<FolderItemWithMediaItems> = arrayListOf(),
    private val selectionCallback: (selectedList: ArrayList<FolderItemWithMediaItems>) -> Unit,
    private val functionCallback: (isShowDialog: Boolean, folderItemWithMedia: FolderItemWithMediaItems) -> Unit
) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {

    fun setList(mediaItems: ArrayList<FolderItemWithMediaItems>) {
        this.folderItems = mediaItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (isFromBottomSheet) {
            val binding =
                ItemAlbumsDialogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val binding =
                ItemAlbumsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = folderItems[position]

        if (isFromBottomSheet) {
            holder.binding as ItemAlbumsDialogBinding

            if (item.folderItem.id == -1) {
                holder.binding.tvFolderName.text = baseActivity.getString(R.string.create_album)
                holder.binding.tvFolderItemSize.gone()
            } else {
                holder.binding.tvFolderName.text = "${item.folderItem.folderName}"
                holder.binding.tvFolderItemSize.visible()
            }
            if (item.folderItem.id == -1) {
                Glide.with(baseActivity).load(R.drawable.ic_bs_new_ph).error(R.mipmap.ic_launcher)
                    .into(holder.binding.ivThumb)
            } else {
                if (item.mediaItems.isNotEmpty()) {
                    if (isAudio) {
                        Glide.with(baseActivity).load(R.drawable.ic_my_audio_ph_bs)
                            .into(holder.binding.ivThumb)
                    } else {
                        Glide.with(baseActivity).load(item.mediaItems[0].hidePath).centerCrop()
                            .sizeMultiplier(0.5f).into(holder.binding.ivThumb)
                    }
                } else {
                    if (isAudio) {
                        Glide.with(baseActivity).load(R.drawable.ic_my_audio_ph_bs)
                            .into(holder.binding.ivThumb)
                    } else if (isDoc) {
                        Glide.with(baseActivity).load(R.drawable.ic_my_videos_ph_bs)
                            .into(holder.binding.ivThumb)
                    } else if (isVideo) {
                        Glide.with(baseActivity).load(R.drawable.ic_my_videos_ph_bs)
                            .into(holder.binding.ivThumb)
                    } else {
                        Glide.with(baseActivity).load(R.drawable.ic_my_photos_ph_bs)
                            .into(holder.binding.ivThumb)
                    }
                }
            }

            holder.binding.tvFolderItemSize.text =
                "${item.mediaItems.size} ${baseActivity.getString(R.string.items)}"
            holder.binding.root.setOnClickListener(1000L) {
                if (item.folderItem.id == -1) {
                    functionCallback.invoke(true, item)
                } else {
                    functionCallback.invoke(false, item)
                }
            }
        } else {
            holder.binding as ItemAlbumsBinding
            holder.binding.cbSelected.isEnabled = false
            holder.binding.cbSelected.isChecked = checkIsItemSelected(item)

            if (item.folderItem.id == -1) {
                holder.binding.tvFolderName.inVisible()
                holder.binding.tvFolderItemSize.inVisible()

                Glide.with(baseActivity).load(R.drawable.ic_add_ph).into(holder.binding.ivThumb)
            } else {
                holder.binding.tvFolderName.visible()
                holder.binding.tvFolderItemSize.visible()

                if (item.mediaItems.isNotEmpty()) {
                    if (isAudio) {
                        Glide.with(baseActivity).load(R.drawable.ic_my_audio_ph)
                            .into(holder.binding.ivThumb)
                    } else {
                        Glide.with(baseActivity).load(item.mediaItems[0].hidePath).centerCrop()
                            .sizeMultiplier(0.5f).into(holder.binding.ivThumb)
                    }
                } else {
                    if (isAudio) {
                        Glide.with(baseActivity).load(R.drawable.ic_my_audio_ph)
                            .into(holder.binding.ivThumb)
                    } else if (isDoc) {
                        Glide.with(baseActivity).load(R.drawable.ic_my_videos_ph)
                            .into(holder.binding.ivThumb)
                    } else if (isVideo) {
                        Glide.with(baseActivity).load(R.drawable.ic_my_videos_ph)
                            .into(holder.binding.ivThumb)
                    } else {
                        Glide.with(baseActivity).load(R.drawable.ic_my_photos_ph)
                            .into(holder.binding.ivThumb)
                    }
                }
            }

            if (isSelectionEnable) {
                if (item.folderItem.id == -1) {
                    holder.binding.cbSelected.gone()
                } else {
                    holder.binding.cbSelected.visible()
                }
                holder.binding.root.setOnLongClickListener(null)
            } else {
                holder.binding.cbSelected.gone()
                holder.binding.root.setOnLongClickListener {

                    if (item.folderItem.id == -1) {
                        isSelectionEnable = false
                        functionCallback.invoke(true, item)
                        false
                    } else {
                        isSelectionEnable = true

                        if (item.folderItem.id == -1) {
                            isSelectionEnable = false
                            false
                        } else {
                            if (selectedList.contains(item)) {
                                selectedList.remove(item)
                            } else {
                                selectedList.add(item)
                            }
                            notifyItemChanged(position)
                            selectionCallback.invoke(selectedList)
                            notifyDataSetChanged()
                            true
                        }


                    }


                }
            }

            holder.binding.tvFolderName.text = "${item.folderItem.folderName}"
            holder.binding.tvFolderItemSize.text = "${item.mediaItems.size}"
            holder.binding.root.setOnClickListener(1000L) {
                if (isSelectionEnable) {
                    if (item.folderItem.id == -1) {
                        baseActivity.showToast(baseActivity.getString(R.string.msg_folder_remove))
                    } else {
                        if (selectedList.contains(item)) {
                            selectedList.remove(item)
                        } else {
                            selectedList.add(item)
                        }
                        notifyItemChanged(position)
                        selectionCallback.invoke(selectedList)
                    }
                } else {
                    if (item.folderItem.id == -1) {
                        functionCallback.invoke(true, item)
                    } else {
                        val mediaItems = arrayListOf<MediaItem>()
                        mediaItems.addAll(item.mediaItems)
                        if (item.mediaItems.isNotEmpty()) {
                            val intent = Intent(baseActivity, AlbumActivity::class.java)
                            intent.putExtra("folderName", item.folderItem.folderName)
                            intent.putExtra("isVideo", isVideo)
                            intent.putExtra("isAudio", isAudio)
                            intent.putExtra("isDoc", isDoc)
                            baseActivity.startActivity(intent)
                        } else {
                            baseActivity.showToast(baseActivity.getString(R.string.no_data_found))
                        }
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return folderItems.size
    }

    private fun checkIsItemSelected(item: FolderItemWithMediaItems): Boolean {
        return selectedList.contains(item)
    }

}