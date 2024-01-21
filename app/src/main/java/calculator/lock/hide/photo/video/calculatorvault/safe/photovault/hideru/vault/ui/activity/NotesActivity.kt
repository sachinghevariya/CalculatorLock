package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.content.Intent
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityNotesBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.adapter.NotesAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.dialog.TrashMessageDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showMyDialog
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class NotesActivity : BaseActivity<ActivityNotesBinding>() {
    override fun getViewBinding() = ActivityNotesBinding.inflate(layoutInflater)

    private var selectedList = ArrayList<NoteItem>()
    private val viewModel: FileManagerViewModel by viewModels()
    private var isAllSelected = false
    private lateinit var notesAdapter: NotesAdapter
    private var notesList = arrayListOf<NoteItem>()
    private var isAdShown = false

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
        binding.tvNoMsgData.isSelected = true
    }

    override fun initData() {
        bannerAd()
        marqueEnable()
        binding.addNewNote.setOnClickListener(1000L) {
            startActivity(Intent(this, NoteEditActivity::class.java).putExtra("isNewNote", true))
        }
        binding.addNote.setOnClickListener(1000L) {
            binding.containerAllSelection.gone()
            selectedList = arrayListOf()
            updateImageList(false)
            startActivity(Intent(this, NoteEditActivity::class.java).putExtra("isNewNote", true))
        }
        binding.ivBack.setOnClickListener(1000L) {
            finish()
        }

        notesAdapter = NotesAdapter(this, arrayListOf(), false, selectedList) { list, noteIem ->
            if (noteIem != null) {
                startActivity(
                    Intent(this, NoteEditActivity::class.java).putExtra(
                        "noteItem",
                        noteIem
                    )
                )
            } else {
                selectedList = list
                if (selectedList.size > 0) {
                    manageUi(true)
                    if (notesList.size != selectedList.size) {
                        binding.cbAll.isChecked = false
                        isAllSelected = false
                    }

                    if (notesList.size == selectedList.size) {
                        binding.cbAll.isChecked = true
                        isAllSelected = true
                    }
                } else {
                    binding.cbAll.isChecked = false
                    isAllSelected = false
                    binding.containerAllSelection.gone()
                    updateImageList(isAllSelected)
                }
            }

        }
        binding.rvNotes.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvNotes.adapter = notesAdapter
        lifecycleScope.launch {
            viewModel.allNotes.observe(this@NotesActivity) {
                if (it.isNotEmpty()) {
                    binding.lvAdd.gone()
                    binding.containerView.visible()

                    notesList.clear()
                    notesList.addAll(it)
                    notesAdapter.setList(notesList)

                } else {
                    binding.lvAdd.visible()
                    binding.containerView.gone()
                }

            }
        }

        binding.viewAll.setOnClickListener(400L) {
            if (binding.cbAll.isChecked) {
                binding.cbAll.isChecked = false
                isAllSelected = false
            } else {
                binding.cbAll.isChecked = true
                isAllSelected = true
            }

            if (isAllSelected) {
                selectedList.clear()
                selectedList.addAll(notesList)
            } else {
                binding.containerAllSelection.gone()
                selectedList = arrayListOf()
            }
            updateImageList(isAllSelected)
        }

        binding.ivDelete.setOnClickListener(1000L) {
            showMyDialog(
                this,
                TrashMessageDialog {
                    CoroutineScope(Dispatchers.IO).launch {
                        val selectedListCopy = ArrayList(selectedList)
                        val deferredList = selectedListCopy.map { noteItem ->
                            async(Dispatchers.IO) {
                                selectedList.remove(noteItem)
                                viewModel.removeNoteItemToDbSoft(noteItem)
                            }
                        }
                        deferredList.awaitAll()
                        withContext(Dispatchers.Main) {
                            updateImageList(false)
                        }
                    }
                })
        }

    }

    private fun manageUi(isSelectionModeOn: Boolean) {
        if (isSelectionModeOn) {
            binding.containerAllSelection.visible()
        } else {
            binding.containerAllSelection.gone()
        }
    }

    private fun updateImageList(isAllSelected: Boolean) {
        notesAdapter.selectedList = selectedList
        notesAdapter.isSelectionEnable = isAllSelected
        manageUi(isAllSelected)
        notesAdapter.setList(notesList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun networkStateChanged(state: NetworkChangeReceiver.NetworkState?) {
        super.networkStateChanged(state)
        if (state == NetworkChangeReceiver.NetworkState.CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).visible()
        } else if (state == NetworkChangeReceiver.NetworkState.NOT_CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).gone()
        }
    }

}