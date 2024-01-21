package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemsFragmentBinding
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.VIEW_TYPE_LIST
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.commons.views.MyFloatingActionButton

abstract class MyViewPagerFragment<BINDING : MyViewPagerFragment.InnerBinding>(context: Context, attributeSet: AttributeSet) :
    RelativeLayout(context, attributeSet) {
    protected var activity: SimpleActivity? = null
    protected var currentViewType = VIEW_TYPE_LIST

    var currentPath = ""
    var isGetContentIntent = false
    var isPickMultipleIntent = false
    var wantedMimeTypes = listOf("")
    var imagesMimeTypes = arrayListOf<String>(
        "image/png",
        "image/jpeg",
        "image/jpg",
        "image/gif",
        "image/bmp",
        "image/webp",
        "image/webp2",
        "image/heif",
        "image/x-icon",
        "image/tiff",
        "image/tif",
        "image/svg+xml",
        "image/vnd.wap.wbmp"
    )
    var videoMimeTypes = arrayListOf<String>(
        "video/mp4",
        "video/3gpp",
        "video/avi",
        "video/x-matroska",
        "video/quicktime",
        "video/3gpp2",
        "video/x-ms-wmv",
        "video/x-flv",
        "video/webm",
        "video/mpeg",
        "video/x-ms-asf",
        "video/mp2t",
        "video/x-m4v",
        "video/divx",
        "application/vnd.rn-realmedia",
        "video/x-flv",
        "video/ogg",
        "video/hevc"
    )

    val audioMimeTypes = arrayListOf<String>(
        "audio/mpeg",
        "audio/mp3",
        "audio/ogg",
        "audio/wav",
        "audio/x-ms-wma",
        "audio/aac",
        "audio/flac",
        "audio/x-matroska",
        "audio/x-aiff",
        "audio/x-flac",
        "audio/x-wav",
        "audio/x-mpegurl",
        "audio/x-ms-asx",
        "audio/x-ms-wax",
        "audio/x-ms-wax",
        "audio/x-pn-realaudio",
        "audio/vnd.rn-realaudio",
        "audio/x-rn-3gpp-amr",
        "audio/x-mp4a-latm"
    )


    protected var isCreateDocumentIntent = false
    protected lateinit var innerBinding: BINDING

    protected fun clickedPath(path: String) {
        if (isGetContentIntent || isCreateDocumentIntent) {
            (activity as FoldersActivity).pickedPath(path)
        } else {
            activity?.tryOpenPathIntent(path, false)
        }
    }

    fun updateIsCreateDocumentIntent(isCreateDocumentIntent: Boolean) {
        this.isCreateDocumentIntent = isCreateDocumentIntent
    }

    fun handleFileDeleting(files: ArrayList<FileDirItem>, hasFolder: Boolean) {
        val firstPath = files.firstOrNull()?.path
        if (firstPath == null || firstPath.isEmpty() || context == null) {
            return
        }

        if (context!!.isPathOnRoot(firstPath)) {
            RootHelpers(activity!!).deleteFiles(files)
        } else {
            (activity as SimpleActivity).deleteFiles(files, hasFolder) {
                if (!it) {
                    activity!!.runOnUiThread {
                        activity!!.toast(R.string.unknown_error_occurred)
                    }
                }
            }
        }
    }

    protected fun isProperMimeType(wantedMimeType: String, path: String, isDirectory: Boolean): Boolean {
        return if (wantedMimeType.isEmpty() || wantedMimeType == "*/*" || isDirectory) {
            true
        } else {
            val fileMimeType = path.getMimeType()
            if (wantedMimeType.endsWith("/*")) {
                fileMimeType.substringBefore("/").equals(wantedMimeType.substringBefore("/"), true)
            } else {
                fileMimeType.equals(wantedMimeType, true)
            }
        }
    }

    abstract fun setupFragment(activity: SimpleActivity)

    abstract fun onResume(textColor: Int)

    abstract fun refreshFragment()

    abstract fun searchQueryChanged(text: String)

    interface InnerBinding {
        val itemsFab: MyFloatingActionButton?
    }

    class ItemsInnerBinding(val binding: ItemsFragmentBinding) : InnerBinding {
        override val itemsFab: MyFloatingActionButton = binding.itemsFab
    }


}
