package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.activity

import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.activity.viewModels
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityNoteEditBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base.BaseActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels.FileManagerViewModel
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.generateRandomInt
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.showToast
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import com.simplemobiletools.commons.extensions.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver

@AndroidEntryPoint
class NoteEditActivity : BaseActivity<ActivityNoteEditBinding>() {
    override fun getViewBinding() = ActivityNoteEditBinding.inflate(layoutInflater)

    private var isNewNote = false
    private lateinit var noteItem: NoteItem
    private val viewModel: FileManagerViewModel by viewModels()

    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
    }

    override fun initData() {
        marqueEnable()
        bannerAd()
        isNewNote = intent.getBooleanExtra("isNewNote", false)
        binding.ivBack.setOnClickListener(1000L) {
           finish()
        }

        if (isNewNote) {

        } else {
            noteItem = intent.getSerializableExtra("noteItem") as NoteItem
            binding.editTextTitle.setText("${noteItem.noteTitle}")
            binding.editTextNotes.setText("${noteItem.noteDescription}")
        }

        binding.ivDone.setOnClickListener(1000L) {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextNotes.text.toString().trim()

            if (isNewNote) {
                if (title.isEmpty()) {
                    showToast(getString(R.string.toast_title))
                } else if (description.isEmpty()) {
                    showToast(getString(R.string.toast_des))
                } else {
                    val noteItem = NoteItem(
                        generateRandomInt(),
                        title,
                        description,
                        MediaType.NOTE,
                        0,
                        "",
                        System.currentTimeMillis()
                    )
                    viewModel.addNoteToDb(noteItem)
                    showInterstitial{
                        finish()
                    }
                }
            } else {
                if (title.isEmpty()) {
                    showToast(getString(R.string.toast_title))
                } else if (description.isEmpty()) {
                    showToast(getString(R.string.toast_des))
                } else {
                    noteItem.noteTitle = title
                    noteItem.noteDescription = description
                    noteItem.createdDate = System.currentTimeMillis()
                    viewModel.updateNoteToDb(noteItem)
                    showInterstitial{
                        finish()
                    }
                }
            }

        }

        binding.editTextTitle.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.editTextNotes.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        showKeyboard(binding.editTextTitle)
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