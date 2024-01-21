package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import com.simplemobiletools.commons.extensions.getProperTextColor
import com.simplemobiletools.commons.helpers.TAB_FILES

class ViewPagerAdapter(val activity: SimpleActivity, val tabsToShow: ArrayList<Int>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layout = getFragment(position)
        val view = activity.layoutInflater.inflate(layout, container, false)
        container.addView(view)

        (view as MyViewPagerFragment<*>).apply {
            val isGetContentIntent = activity.intent.action == Intent.ACTION_GET_CONTENT || activity.intent.action == Intent.ACTION_PICK
            val isCreateDocumentIntent = activity.intent.action == Intent.ACTION_CREATE_DOCUMENT
            val allowPickingMultipleIntent = activity.intent.getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            val getContentMimeType = if (isGetContentIntent) {
                activity.intent.type ?: ""
            } else {
                ""
            }

            val passedExtraMimeTypes = activity.intent.getStringArrayExtra(Intent.EXTRA_MIME_TYPES)
            val extraMimeTypes = if (isGetContentIntent && passedExtraMimeTypes != null) {
                passedExtraMimeTypes
            } else {
                null
            }

            this.isPickMultipleIntent = allowPickingMultipleIntent
            this.isGetContentIntent = isGetContentIntent
            wantedMimeTypes = extraMimeTypes?.toList() ?: listOf(getContentMimeType)
            updateIsCreateDocumentIntent(isCreateDocumentIntent)

            setupFragment(activity)
            onResume(activity.getProperTextColor())
        }

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        container.removeView(item as View)
    }

    override fun getCount() = tabsToShow.filter { it and activity.config.showTabs != 0 }.size

    override fun isViewFromObject(view: View, item: Any) = view == item

    private fun getFragment(position: Int): Int {
        val showTabs = activity.config.showTabs
        val fragments = arrayListOf<Int>()
        if (showTabs and TAB_FILES != 0) {
            fragments.add(R.layout.items_fragment)
        }
        return fragments[position]
    }
}
