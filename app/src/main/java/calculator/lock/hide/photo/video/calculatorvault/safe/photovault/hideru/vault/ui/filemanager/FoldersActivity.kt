package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.databinding.ActivityMainNewBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.gone
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.visible
import com.simplemobiletools.commons.dialogs.ConfirmationAdvancedDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.*
import com.stericson.RootTools.RootTools
import dagger.hilt.android.AndroidEntryPoint
import plugin.adsdk.extras.NetworkChangeReceiver
import plugin.adsdk.service.AdsUtility
import plugin.adsdk.service.AppOpenManager
import java.io.File
import kotlin.system.exitProcess

@AndroidEntryPoint
class FoldersActivity : SimpleActivity() {
    companion object {
        private const val BACK_PRESS_TIMEOUT = 5000
        private const val MANAGE_STORAGE_RC = 201
        private const val PICKED_PATH = "picked_path"
        const val APPLICATION_ID =
            "calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault"
    }

    private val binding by viewBinding(ActivityMainNewBinding::inflate)
    private var receiver: NetworkChangeReceiver? = null
    private var wasBackJustPressed = false
    private var mIsPasswordProtectionPending = false
    private var mWasProtectionHandled = false
    private var mTabsToShow = ArrayList<Int>()

    private var mStoredFontSize = 0
    private var mStoredDateFormat = ""
    private var mStoredTimeFormat = ""
    private var mStoredShowTabs = 0
    private fun marqueEnable() {
        binding.tvHeader.isSelected = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        marqueEnable()
        bannerAd(findViewById(R.id.banner_ad_container))

        isVideo = intent.getBooleanExtra("isVideo", false)
        isAudio = intent.getBooleanExtra("isAudio", false)

        appLaunched(APPLICATION_ID)
        mTabsToShow = getTabsList()

        if (!config.wasStorageAnalysisTabAdded && isOreoPlus()) {
            config.wasStorageAnalysisTabAdded = true
            if (config.showTabs and TAB_STORAGE_ANALYSIS == 0) {
                config.showTabs += TAB_STORAGE_ANALYSIS
            }
        }

        storeStateVariables()
        setupTabs()

        updateMaterialActivityViews(
            binding.mainCoordinator, null, useTransparentNavigation = false, useTopSearchMenu = true
        )

        mIsPasswordProtectionPending = config.isAppPasswordProtectionOn

        binding.ivBack.setOnClickListener(1000L) {
            val currentFragment = getCurrentFragment()
            if ((currentFragment as ItemsFragment).getBreadcrumbs().getItemCount() <= 1) {
                finish()
            } else {
                currentFragment.getBreadcrumbs().removeBreadcrumb()
                openPath(currentFragment.getBreadcrumbs().getLastItem().path)
            }
        }

        if (savedInstanceState == null) {
            handleAppPasswordProtection {
                mWasProtectionHandled = it
                if (it) {
                    initFragments()
                    mIsPasswordProtectionPending = false
                    tryInitFileManager()
                    checkIfRootAvailable()
                    checkInvalidFavorites()
                } else {
                    finish()
                }
            }
        }

        setupNetworkChangeReceiver()
    }

    fun bannerAd(viewGroup: ViewGroup) {
        if (TextUtils.isEmpty(AdsUtility.config.adMob.bannerAd)) {
            viewGroup.visibility = View.GONE
            return
        }
        if (AdsUtility.optimizeAppOpen()) {
            AppOpenManager.blockAppOpen(this) //limiting app-open
        }
        AdsUtility.requestBannerAd(this, viewGroup)
    }

    override fun onResume() {
        super.onResume()
        if (mStoredShowTabs != config.showTabs) {
            config.lastUsedViewPagerPage = 0
            exitProcess(0)
            return
        }

        updateMenuColors()

        getAllFragments().forEach {
            it?.onResume(getProperTextColor())
        }

        if (mStoredFontSize != config.fontSize) {
            getAllFragments().forEach {
                (it as? ItemOperationsListener)?.setupFontSize()
            }
        }

        if (mStoredDateFormat != config.dateFormat || mStoredTimeFormat != getTimeFormat()) {
            getAllFragments().forEach {
                (it as? ItemOperationsListener)?.setupDateTimeFormat()
            }
        }

        if (binding.mainViewPager.adapter == null && mWasProtectionHandled) {
            initFragments()
        }
    }

    override fun onPause() {
        super.onPause()
        storeStateVariables()
        config.lastUsedViewPagerPage = binding.mainViewPager.currentItem
    }

    override fun onDestroy() {
        super.onDestroy()
        config.temporarilyShowHidden = false
    }

    override fun onBackPressed() {
        val currentFragment = getCurrentFragment()
        if ((currentFragment as ItemsFragment).getBreadcrumbs().getItemCount() <= 1) {
            finish()
        } else {
            currentFragment.getBreadcrumbs().removeBreadcrumb()
            openPath(currentFragment.getBreadcrumbs().getLastItem().path)
        }
    }

    /*private fun baseBackPressed(callBack: BaseActivity.OnAdClose, isFromSystem: Boolean = false) {
        if (isFromSystem) {
            InterstitialAdsHelper.showBackInterstitialAds(this@FoldersActivity,
                false,
                object : InterstitialAdsHelper.OnInterstitialAdsListener {
                    override fun onFinishInterstitialAds() {
                        callBack.onAdClose()
                    }
                })
        } else {
            if (AdsUtils.adModel?.adOnBack!!) {
                InterstitialAdsHelper.showBackInterstitialAds(this@FoldersActivity,
                    true,
                    object : InterstitialAdsHelper.OnInterstitialAdsListener {
                        override fun onFinishInterstitialAds() {
                            callBack.onAdClose()
                        }
                    })

            } else {
                callBack.onAdClose()
            }
        }
    }
*/
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PICKED_PATH, getItemsFragment()?.currentPath ?: "")
        outState.putBoolean(WAS_PROTECTION_HANDLED, mWasProtectionHandled)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mWasProtectionHandled = savedInstanceState.getBoolean(WAS_PROTECTION_HANDLED, false)
        val path = savedInstanceState.getString(PICKED_PATH) ?: internalStoragePath

        if (binding.mainViewPager.adapter == null) {
            binding.mainViewPager.onGlobalLayout {
                restorePath(path)
            }
        } else {
            restorePath(path)
        }
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        isAskingPermissions = false
        if (requestCode == MANAGE_STORAGE_RC && isRPlus()) {
            actionOnPermission?.invoke(Environment.isExternalStorageManager())
        }
    }

    private fun restorePath(path: String) {
        if (!mWasProtectionHandled) {
            handleAppPasswordProtection {
                mWasProtectionHandled = it
                if (it) {
                    mIsPasswordProtectionPending = false
                    openPath(path, true)
                } else {
                    finish()
                }
            }
        } else {
            openPath(path, true)
        }
    }

    private fun updateMenuColors() {
        updateStatusbarColor(getProperBackgroundColor())
    }

    private fun storeStateVariables() {
        config.apply {
            mStoredFontSize = fontSize
            mStoredDateFormat = dateFormat
            mStoredTimeFormat = context.getTimeFormat()
            mStoredShowTabs = showTabs
        }
    }

    private fun tryInitFileManager() {
        val hadPermission = hasStoragePermission()
        handleStoragePermission {
            checkOTGPath()
            if (it) {
                if (binding.mainViewPager.adapter == null) {
                    initFragments()
                }

                binding.mainViewPager.onGlobalLayout {
                    initFileManager(!hadPermission)
                }
            } else {
                toast(com.simplemobiletools.commons.R.string.no_storage_permissions)
                finish()
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun handleStoragePermission(callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (hasStoragePermission()) {
            callback(true)
        } else {
            if (isRPlus()) {
                ConfirmationAdvancedDialog(
                    this,
                    "",
                    com.simplemobiletools.commons.R.string.access_storage_prompt,
                    R.string.ok,
                    0,
                    false
                ) { success ->
                    if (success) {
                        isAskingPermissions = true
                        actionOnPermission = callback
                        try {
                            val intent =
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                            intent.addCategory("android.intent.category.DEFAULT")
                            intent.data = Uri.parse("package:$packageName")
                            startActivityForResult(intent, MANAGE_STORAGE_RC)
                        } catch (e: Exception) {
                            showErrorToast(e)
                            val intent = Intent()
                            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                            startActivityForResult(intent, MANAGE_STORAGE_RC)
                        }
                    } else {
                        finish()
                    }
                }
            } else {
                handlePermission(PERMISSION_WRITE_STORAGE, callback)
            }
        }
    }

    private fun initFileManager(refreshRecents: Boolean) {
        if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
            val data = intent.data
            if (data?.scheme == "file") {
                openPath(data.path!!)
            } else {
                val path = getRealPathFromURI(data!!)
                if (path != null) {
                    openPath(path)
                } else {
                    openPath(config.homeFolder)
                }
            }

            if (!File(data.path!!).isDirectory) {
                tryOpenPathIntent(data.path!!, false, finishActivity = true)
            }

            binding.mainViewPager.currentItem = 0
        } else {
            openPath(config.homeFolder)
        }
    }

    private fun initFragments() {
        binding.mainViewPager.apply {
            adapter = ViewPagerAdapter(this@FoldersActivity, mTabsToShow)
            offscreenPageLimit = 2
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    getAllFragments().forEach {
                        (it as? ItemOperationsListener)?.finishActMode()
                    }
                }
            })
            currentItem = config.lastUsedViewPagerPage


        }
    }

    private fun setupTabs() {
        mTabsToShow = arrayListOf(TAB_FILES)
    }

    private fun checkOTGPath() {
        ensureBackgroundThread {
            if (!config.wasOTGHandled && hasPermission(PERMISSION_WRITE_STORAGE) && hasOTGConnected() && config.OTGPath.isEmpty()) {
                getStorageDirectories().firstOrNull {
                    it.trimEnd('/') != internalStoragePath && it.trimEnd(
                        '/'
                    ) != sdCardPath
                }?.apply {
                    config.wasOTGHandled = true
                    config.OTGPath = trimEnd('/')
                }
            }
        }
    }

    private fun openPath(path: String, forceRefresh: Boolean = false) {
        if (mIsPasswordProtectionPending && !mWasProtectionHandled) {
            return
        }

        var newPath = path
        val file = File(path)
        if (config.OTGPath.isNotEmpty() && config.OTGPath == path.trimEnd('/')) {
            newPath = path
        } else if (file.exists() && !file.isDirectory) {
            newPath = file.parent
        } else if (!file.exists() && !isPathOnOTG(newPath)) {
            newPath = internalStoragePath
        }

        getItemsFragment()?.openPath(newPath, forceRefresh)
    }

    private fun goHome() {
        if (config.homeFolder != getCurrentFragment()!!.currentPath) {
            openPath(config.homeFolder)
        }
    }

    fun updateFragmentColumnCounts() {
        getAllFragments().forEach {
            (it as? ItemOperationsListener)?.columnCountChanged()
        }
    }

    private fun checkIfRootAvailable() {
        ensureBackgroundThread {
            config.isRootAvailable = RootTools.isRootAvailable()
            if (config.isRootAvailable && config.enableRootAccess) {
                RootHelpers(this).askRootIfNeeded {
                    config.enableRootAccess = it
                }
            }
        }
    }

    private fun checkInvalidFavorites() {
        ensureBackgroundThread {
            config.favorites.forEach {
                if (!isPathOnOTG(it) && !isPathOnSD(it) && !File(it).exists()) {
                    config.removeFavorite(it)
                }
            }
        }
    }

    fun pickedPath(path: String) {
        val resultIntent = Intent()
        val uri = getFilePublicUri(File(path), APPLICATION_ID)
        val type = path.getMimeType()
        resultIntent.setDataAndType(uri, type)
        resultIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    fun pickedPaths(paths: ArrayList<String>) {
        val newPaths = paths.map { getFilePublicUri(File(it), APPLICATION_ID) } as ArrayList
        val clipData = ClipData(
            "Attachment", arrayOf(paths.getMimeType()), ClipData.Item(newPaths.removeAt(0))
        )

        newPaths.forEach {
            clipData.addItem(ClipData.Item(it))
        }

        Intent().apply {
            this.clipData = clipData
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    private fun getItemsFragment() = findViewById<ItemsFragment>(R.id.items_fragment)

    private fun getAllFragments(): ArrayList<MyViewPagerFragment<*>?> =
        arrayListOf(getItemsFragment())

    private fun getCurrentFragment(): MyViewPagerFragment<*>? {
        val showTabs = config.showTabs
        val fragments = arrayListOf<MyViewPagerFragment<*>>()
        if (showTabs and TAB_FILES != 0) {
            fragments.add(getItemsFragment())
        }

        return fragments.getOrNull(binding.mainViewPager.currentItem)
    }

    private fun getTabsList() = arrayListOf(TAB_FILES)

    private fun setupNetworkChangeReceiver() {
        receiver = NetworkChangeReceiver { state: NetworkChangeReceiver.NetworkState? ->
            networkStateChanged(
                state
            )
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(receiver, filter)
    }

    fun networkStateChanged(state: NetworkChangeReceiver.NetworkState?) {
        if (state == NetworkChangeReceiver.NetworkState.CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).visible()
        } else if (state == NetworkChangeReceiver.NetworkState.NOT_CONNECTED) {
            binding.root.findViewById<FrameLayout>(R.id.banner_ad_container).gone()
        }
    }
}
