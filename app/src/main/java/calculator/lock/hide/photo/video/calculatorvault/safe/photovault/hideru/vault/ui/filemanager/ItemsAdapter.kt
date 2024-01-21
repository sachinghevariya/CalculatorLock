package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemDirGridBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemEmptyBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemFileDirListBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemFileGridBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ItemSectionBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.formatDate
import com.simplemobiletools.commons.extensions.formatSize
import com.simplemobiletools.commons.extensions.getAndroidSAFUri
import com.simplemobiletools.commons.extensions.getColoredDrawableWithColor
import com.simplemobiletools.commons.extensions.getParentPath
import com.simplemobiletools.commons.extensions.getTextSize
import com.simplemobiletools.commons.extensions.getTimeFormat
import com.simplemobiletools.commons.extensions.hasOTGConnected
import com.simplemobiletools.commons.extensions.highlightTextPart
import com.simplemobiletools.commons.extensions.isPathOnOTG
import com.simplemobiletools.commons.extensions.isRestrictedSAFOnlyRoot
import com.simplemobiletools.commons.helpers.VIEW_TYPE_LIST
import com.simplemobiletools.commons.helpers.getFilePlaceholderDrawables
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.commons.views.MyRecyclerView

class ItemsAdapter(
    activity: SimpleActivity,
    var listItems: MutableList<ListItem>,
    val listener: ItemOperationsListener?,
    recyclerView: MyRecyclerView,
    val isPickMultipleIntent: Boolean,
    val swipeRefreshLayout: SwipeRefreshLayout?,
    canHaveIndividualViewType: Boolean = true,
    var selectedItems: ArrayList<ListItem>,
    itemClick: (Any,Int) -> Unit
) : MyRecyclerViewAdapter(activity, recyclerView, itemClick),
    RecyclerViewFastScroller.OnPopupTextUpdate {

    private lateinit var fileDrawable: Drawable
    private lateinit var folderDrawable: Drawable
    private var fileDrawables = HashMap<String, Drawable>()
    private var currentItemsHash = listItems.hashCode()
    private var textToHighlight = ""
    private val hasOTGConnected = activity.hasOTGConnected()
    private var fontSize = 0f
    private var smallerFontSize = 0f
    private var dateFormat = ""
    private var timeFormat = ""

    private val config = activity.config
    private val viewType = if (canHaveIndividualViewType) {
        config.getFolderViewType(
            listItems.firstOrNull { !it.isSectionTitle }?.mPath?.getParentPath() ?: ""
        )
    } else {
        config.viewType
    }
    private val isListViewType = viewType == VIEW_TYPE_LIST
    private var displayFilenamesInGrid = config.displayFilenames

    companion object {
        private const val TYPE_FILE = 1
        private const val TYPE_DIR = 2
        private const val TYPE_SECTION = 3
        private const val TYPE_GRID_TYPE_DIVIDER = 4
    }

    init {
        setupDragListener(true)
        initDrawables()
        updateFontSizes()
        dateFormat = config.dateFormat
        timeFormat = activity.getTimeFormat()
    }


    private fun setSelectedItemList(selectedItems: ArrayList<ListItem>) {
        this.selectedItems.clear()
        this.selectedItems = selectedItems
    }

    override fun getActionMenuId() = R.menu.cab

    override fun prepareActionMode(menu: Menu) {
        menu.apply {
            checkHideBtnVisibility(this)
        }
    }

    override fun actionItemPressed(id: Int) {
        if (selectedKeys.isEmpty()) {
            return
        }
    }

    override fun getSelectableItemCount() =
        listItems.filter { !it.isSectionTitle && !it.isGridTypeDivider }.size

    override fun getIsItemSelectable(position: Int) =
        !listItems[position].isSectionTitle && !listItems[position].isGridTypeDivider

    override fun getItemSelectionKey(position: Int) =
        listItems.getOrNull(position)?.path?.hashCode()

    override fun getItemKeyPosition(key: Int) = listItems.indexOfFirst { it.path.hashCode() == key }

    override fun onActionModeCreated() {
        swipeRefreshLayout?.isRefreshing = false
        swipeRefreshLayout?.isEnabled = false
    }

    override fun onActionModeDestroyed() {
        swipeRefreshLayout?.isEnabled = true
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            listItems[position].isGridTypeDivider -> TYPE_GRID_TYPE_DIVIDER
            listItems[position].isSectionTitle -> TYPE_SECTION
            listItems[position].mIsDirectory -> TYPE_DIR
            else -> TYPE_FILE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = Binding.getByItemViewType(viewType, isListViewType)
            .inflate(layoutInflater, parent, false)

        return createViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val fileDirItem = listItems[position]
        holder.bindView(
            fileDirItem,
            true,
            !fileDirItem.isSectionTitle
        ) { itemView, layoutPosition ->
            val viewType = getItemViewType(position)
            setupView(
                Binding.getByItemViewType(viewType, isListViewType).bind(itemView),
                fileDirItem
            )
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = listItems.size

    private fun checkHideBtnVisibility(menu: Menu) {
        var hiddenCnt = 0
        var unhiddenCnt = 0
        getSelectedFileDirItems().map { it.name }.forEach {
            if (it.startsWith(".")) {
                hiddenCnt++
            } else {
                unhiddenCnt++
            }
        }

    }

    private fun getSelectedFileDirItems() =
        listItems.filter { selectedKeys.contains(it.path.hashCode()) } as ArrayList<FileDirItem>

    fun updateItems(newItems: ArrayList<ListItem>, highlightText: String = "") {
        if (newItems.hashCode() != currentItemsHash) {
            currentItemsHash = newItems.hashCode()
            textToHighlight = highlightText
            listItems = newItems.clone() as ArrayList<ListItem>
            notifyDataSetChanged()
            finishActMode()
        } else if (textToHighlight != highlightText) {
            textToHighlight = highlightText
            notifyDataSetChanged()
        }
    }

    fun updateFontSizes() {
        fontSize = activity.getTextSize()
        smallerFontSize = fontSize * 0.8f
        notifyDataSetChanged()
    }

    fun updateDateTimeFormat() {
        dateFormat = config.dateFormat
        timeFormat = activity.getTimeFormat()
        notifyDataSetChanged()
    }

    fun updateDisplayFilenamesInGrid() {
        displayFilenamesInGrid = config.displayFilenames
        notifyDataSetChanged()
    }

    fun updateChildCount(path: String, count: Int) {
        val position = getItemKeyPosition(path.hashCode())
        val item = listItems.getOrNull(position) ?: return
        item.children = count
        notifyItemChanged(position, Unit)
    }

    fun isASectionTitle(position: Int) = listItems.getOrNull(position)?.isSectionTitle ?: false

    fun isGridTypeDivider(position: Int) = listItems.getOrNull(position)?.isGridTypeDivider ?: false

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (!activity.isDestroyed && !activity.isFinishing) {
            val icon = Binding.getByItemViewType(holder.itemViewType, isListViewType)
                .bind(holder.itemView).itemIcon
            if (icon != null) {
                Glide.with(activity).clear(icon)
            }
        }
    }

    private fun setupView(binding: ItemViewBinding, listItem: ListItem) {
        val isSelected = selectedKeys.contains(listItem.path.hashCode())
        binding.apply {
            itemCb?.isEnabled = false
            itemName?.isSelected = true
            if (listItem.isSectionTitle) {
                itemIcon?.setImageDrawable(folderDrawable)
                itemSection?.text =
                    if (textToHighlight.isEmpty()) listItem.mName else listItem.mName.highlightTextPart(
                        textToHighlight,
                        properPrimaryColor
                    )
                itemSection?.setTextColor(textColor)
                itemSection?.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
            } else if (!listItem.isGridTypeDivider) {
//                root.setupViewBackground(activity)
                itemFrame.isSelected = isSelected
                val fileName = listItem.name
                itemName?.text =
                    if (textToHighlight.isEmpty()) fileName else fileName.highlightTextPart(
                        textToHighlight,
                        properPrimaryColor
                    )
                itemName?.setTextColor(textColor)
                itemName?.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    if (isListViewType) fontSize else smallerFontSize
                )

                itemDetails?.setTextColor(textColor)
                itemDetails?.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)

                itemDate?.setTextColor(textColor)
                itemDate?.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallerFontSize)

                itemCheck?.beVisibleIf(isSelected)
                if (isSelected) {
                    itemCheck?.background?.applyColorFilter(properPrimaryColor)
                    itemCheck?.applyColorFilter(contrastColor)
                }

                if (!isListViewType && !listItem.isDirectory) {
                    itemName?.beVisibleIf(displayFilenamesInGrid)
                } else {
                    itemName?.beVisible()
                }

                if (listItem.isDirectory) {
                    itemIcon?.setImageDrawable(folderDrawable)
                    itemDetails?.text = getChildrenCnt(listItem)
                    itemDate?.beGone()
                    itemCb?.beGone()
                    itemNext?.beVisible()
                } else {

                    itemCb?.isChecked = selectedItems.contains(listItem)

                    itemDetails?.text = listItem.size.formatSize()
                    itemDate?.beVisible()
                    itemDate?.text = listItem.modified.formatDate(activity, dateFormat, timeFormat)
                    itemCb?.beVisible()
                    itemNext?.beGone()
                    val drawable = fileDrawables.getOrElse(
                        fileName.substringAfterLast(".").toLowerCase()
                    ) { fileDrawable }
                    val options = RequestOptions()
                        .signature(listItem.getKey())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(drawable)
                        .transform(CenterCrop(), RoundedCorners(10))

                    val itemToLoad = getImagePathToLoad(listItem.path)
                    if (!activity.isDestroyed && itemIcon != null) {
                        Glide.with(activity)
                            .load(itemToLoad)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(options)
                            .into(itemIcon!!)
                    }
                }
            }
        }
    }

    private fun getChildrenCnt(item: FileDirItem): String {
        val children = item.children
        return activity.resources.getQuantityString(
            com.simplemobiletools.commons.R.plurals.items,
            children,
            children
        )
    }

    private fun getOTGPublicPath(itemToLoad: String) =
        "${baseConfig.OTGTreeUri}/document/${baseConfig.OTGPartition}%3A${
            itemToLoad.substring(
                baseConfig.OTGPath.length
            ).replace("/", "%2F")
        }"

    private fun getImagePathToLoad(path: String): Any {
        var itemToLoad = if (path.endsWith(".apk", true)) {
            val packageInfo =
                activity.packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
            if (packageInfo != null) {
                val appInfo = packageInfo.applicationInfo
                appInfo.sourceDir = path
                appInfo.publicSourceDir = path
                appInfo.loadIcon(activity.packageManager)
            } else {
                path
            }
        } else {
            path
        }

        if (activity.isRestrictedSAFOnlyRoot(path)) {
            itemToLoad = activity.getAndroidSAFUri(path)
        } else if (hasOTGConnected && itemToLoad is String && activity.isPathOnOTG(itemToLoad) && baseConfig.OTGTreeUri.isNotEmpty() && baseConfig.OTGPartition.isNotEmpty()) {
            itemToLoad = getOTGPublicPath(itemToLoad)
        }

        return itemToLoad
    }

    fun initDrawables() {
        folderDrawable = resources.getColoredDrawableWithColor(
            R.drawable.ic_directory,
            textColor
        )
        folderDrawable.alpha = 180
        fileDrawable =
            resources.getDrawable(com.simplemobiletools.commons.R.drawable.ic_file_generic)
        fileDrawables = getFilePlaceholderDrawables(activity)
    }

    override fun onChange(position: Int) =
        listItems.getOrNull(position)?.getBubbleText(activity, dateFormat, timeFormat) ?: ""

    private sealed interface Binding {
        companion object {
            fun getByItemViewType(viewType: Int, isListViewType: Boolean): Binding {
                return when (viewType) {
                    TYPE_SECTION -> ItemSection
                    TYPE_GRID_TYPE_DIVIDER -> ItemEmpty
                    else -> {
                        if (isListViewType) {
                            ItemFileDirList
                        } else if (viewType == TYPE_DIR) {
                            ItemDirGrid
                        } else {
                            ItemFileGrid
                        }
                    }
                }
            }
        }

        fun inflate(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup,
            attachToRoot: Boolean
        ): ItemViewBinding

        fun bind(view: View): ItemViewBinding

        data object ItemSection : Binding {
            override fun inflate(
                layoutInflater: LayoutInflater,
                viewGroup: ViewGroup,
                attachToRoot: Boolean
            ): ItemViewBinding {
                return ItemSectionBindingAdapter(
                    ItemSectionBinding.inflate(
                        layoutInflater,
                        viewGroup,
                        attachToRoot
                    )
                )
            }

            override fun bind(view: View): ItemViewBinding {
                return ItemSectionBindingAdapter(ItemSectionBinding.bind(view))
            }
        }

        data object ItemEmpty : Binding {
            override fun inflate(
                layoutInflater: LayoutInflater,
                viewGroup: ViewGroup,
                attachToRoot: Boolean
            ): ItemViewBinding {
                return ItemEmptyBindingAdapter(
                    ItemEmptyBinding.inflate(
                        layoutInflater,
                        viewGroup,
                        attachToRoot
                    )
                )
            }

            override fun bind(view: View): ItemViewBinding {
                return ItemEmptyBindingAdapter(ItemEmptyBinding.bind(view))
            }
        }

        data object ItemFileDirList : Binding {
            override fun inflate(
                layoutInflater: LayoutInflater,
                viewGroup: ViewGroup,
                attachToRoot: Boolean
            ): ItemViewBinding {
                return ItemFileDirListBindingAdapter(
                    ItemFileDirListBinding.inflate(
                        layoutInflater,
                        viewGroup,
                        attachToRoot
                    )
                )
            }

            override fun bind(view: View): ItemViewBinding {
                return ItemFileDirListBindingAdapter(ItemFileDirListBinding.bind(view))
            }
        }

        data object ItemDirGrid : Binding {
            override fun inflate(
                layoutInflater: LayoutInflater,
                viewGroup: ViewGroup,
                attachToRoot: Boolean
            ): ItemViewBinding {
                return ItemDirGridBindingAdapter(
                    ItemDirGridBinding.inflate(
                        layoutInflater,
                        viewGroup,
                        attachToRoot
                    )
                )
            }

            override fun bind(view: View): ItemViewBinding {
                return ItemDirGridBindingAdapter(ItemDirGridBinding.bind(view))
            }
        }

        data object ItemFileGrid : Binding {
            override fun inflate(
                layoutInflater: LayoutInflater,
                viewGroup: ViewGroup,
                attachToRoot: Boolean
            ): ItemViewBinding {
                return ItemFileGridBindingAdapter(
                    ItemFileGridBinding.inflate(
                        layoutInflater,
                        viewGroup,
                        attachToRoot
                    )
                )
            }

            override fun bind(view: View): ItemViewBinding {
                return ItemFileGridBindingAdapter(ItemFileGridBinding.bind(view))
            }
        }
    }

    private interface ItemViewBinding : ViewBinding {
        val itemFrame: FrameLayout
        val itemName: TextView?
        val itemIcon: ImageView?
        val itemCheck: ImageView?
        val itemDetails: TextView?
        val itemDate: TextView?
        val itemSection: TextView?
        val itemCb: CheckBox?
        val itemNext: ImageView?
        val rootView: View?
    }

    private class ItemSectionBindingAdapter(val binding: ItemSectionBinding) : ItemViewBinding {
        override val itemFrame: FrameLayout = binding.itemFrame
        override val itemName: TextView? = null
        override val itemIcon: ImageView = binding.itemIcon
        override val itemDetails: TextView? = null
        override val itemDate: TextView? = null
        override val itemCheck: ImageView? = null
        override val itemSection: TextView = binding.itemSection
        override val itemCb: CheckBox? = null
        override val itemNext: ImageView? = null
        override val rootView: View? = null
        override fun getRoot(): View = binding.root
    }

    private class ItemEmptyBindingAdapter(val binding: ItemEmptyBinding) : ItemViewBinding {
        override val itemFrame: FrameLayout = binding.itemFrame
        override val itemName: TextView? = null
        override val itemIcon: ImageView? = null
        override val itemDetails: TextView? = null
        override val itemDate: TextView? = null
        override val itemCheck: ImageView? = null
        override val itemSection: TextView? = null
        override val itemCb: CheckBox? = null
        override val itemNext: ImageView? = null
        override val rootView: View? = null
        override fun getRoot(): View = binding.root
    }

    private class ItemFileDirListBindingAdapter(val binding: ItemFileDirListBinding) :
        ItemViewBinding {
        override val itemFrame: FrameLayout = binding.itemFrame
        override val itemName: TextView = binding.itemName
        override val itemIcon: ImageView = binding.itemIcon
        override val itemDetails: TextView = binding.itemDetails
        override val itemDate: TextView = binding.itemDate
        override val itemCheck: ImageView? = null
        override val itemSection: TextView? = null
        override val itemCb: CheckBox = binding.cbSelected
        override val itemNext: ImageView = binding.ivNext
        override val rootView: View = binding.rootView
        override fun getRoot(): View = binding.root
    }

    private class ItemDirGridBindingAdapter(val binding: ItemDirGridBinding) : ItemViewBinding {
        override val itemFrame: FrameLayout = binding.itemFrame
        override val itemName: TextView = binding.itemName
        override val itemIcon: ImageView = binding.itemIcon
        override val itemDetails: TextView? = null
        override val itemDate: TextView? = null
        override val itemCheck: ImageView = binding.itemCheck
        override val itemSection: TextView? = null
        override val itemCb: CheckBox? = null
        override val itemNext: ImageView? = null
        override val rootView: View? = null
        override fun getRoot(): View = binding.root
    }

    private class ItemFileGridBindingAdapter(val binding: ItemFileGridBinding) : ItemViewBinding {
        override val itemFrame: FrameLayout = binding.itemFrame
        override val itemName: TextView = binding.itemName
        override val itemIcon: ImageView = binding.itemIcon
        override val itemDetails: TextView? = null
        override val itemDate: TextView? = null
        override val itemCheck: ImageView? = null
        override val itemSection: TextView? = null
        override val itemCb: CheckBox? = null
        override val itemNext: ImageView? = null
        override val rootView: View? = null
        override fun getRoot(): View = binding.root
    }
}
