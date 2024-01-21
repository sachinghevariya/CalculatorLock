package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.base

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.AdsUtils
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.setOnClickListener
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.MyPreferences
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.simplemobiletools.commons.extensions.addBit
import com.simplemobiletools.commons.extensions.isUsingGestureNavigation
import com.simplemobiletools.commons.extensions.navigationBarHeight
import com.simplemobiletools.commons.extensions.onApplyWindowInsets
import com.simplemobiletools.commons.extensions.removeBit
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl
import plugin.adsdk.service.AdsUtility
import plugin.adsdk.service.BaseActivity
import java.util.Locale

abstract class BaseActivity<B : ViewBinding> : BaseActivity() {

    lateinit var binding: B
    lateinit var myPreferences: MyPreferences
    abstract fun getViewBinding(): B

    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        val decorView = window.decorView
        var newUiOptions = decorView.systemUiVisibility
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
        newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = newUiOptions
        super.onCreate(savedInstanceState)
        myPreferences = MyPreferences.getPreferences(this)!!
        localeDelegate.onCreate(this)
        binding = getViewBinding()
        setContentView(binding.root)
        initData()
        setupNetworkChangeReceiver()
    }

    private fun handleNavigationAndScrolling() {
        if (navigationBarHeight > 0 || isUsingGestureNavigation()) {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.addBit(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            // Don't touch this. Window Inset API often has a domino effect and things will most likely break.
            onApplyWindowInsets {
                val insets =
                    it.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            }
        } else {
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility.removeBit(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }


    fun showExitDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_exit)
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_card_edit)
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.findViewById<TextView>(R.id.tvShare).isSelected = true
        dialog.findViewById<TextView>(R.id.tvQuit).isSelected = true
        dialog.findViewById<TextView>(R.id.tvExitTextTitle).isSelected = true
        dialog.findViewById<TextView>(R.id.tvExitTextTitle1).isSelected = true

        dialog.findViewById<TextView>(R.id.tvShare).setOnClickListener(500L) {
            dialog.dismiss()
            AdsUtility.rateUs(this)
        }
        dialog.findViewById<TextView>(R.id.tvQuit).setOnClickListener(500L) {
            dialog.dismiss()
            AdsUtils.logActivityOpen(this, "MainScreen", "AppExited")
//            exitProcess(0)
            finishAffinity()
        }
        dialog.show()
    }

    var appOpenBlockLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {}

    val REQUEST_CODE_PERMISSION = 3256

    open fun permissionsNotification(): Array<String>? {
        val p: Array<String> = if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission33Notification
        } else {
            arrayOf()
        }
        return p
    }

    fun permissionsStorage(): Array<String> {
        val p: Array<String> = if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission33
        } else {
            permissionAll
        }
        return p
    }

    private var permissionQ = arrayOf<String>(
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
    )

    private var permissionAll = arrayOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private var permission33 = arrayOf<String>(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.POST_NOTIFICATIONS
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private var permission33Notification = arrayOf<String>(
        Manifest.permission.POST_NOTIFICATIONS
    )

    open fun checkRunTimePermission(
        vararg permissionArrays: String?,
        listener: () -> Unit
    ) {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermissions(*permissionArrays)) {
                listener.invoke()
            } else {
                Dexter.withContext(this).withPermissions(*permissionArrays)
                    .withListener(CompositeMultiplePermissionsListener(object :
                        MultiplePermissionsListener {
                        override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                            if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                listener.invoke()
                            } else {
                                showPermissionSnack()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            list: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                            permissionToken: PermissionToken?
                        ) {
                            permissionToken?.continuePermissionRequest()
                        }

                    })).onSameThread().check()
            }
        } else {

        }
    }

    override fun hasPermissions(vararg permissions: String?): Boolean {
        if (permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        this, permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    open fun showPermissionSnack() {
        val message = "All those permissions are needed for working all functionality"
        val snackbar = Snackbar.make(
            findViewById<View>(android.R.id.content),
            message,
            BaseTransientBottomBar.LENGTH_INDEFINITE
        ).setAction("Settings") { _: View? -> openSettingsDialog() }
        snackbar.setActionTextColor(Color.RED)
        val sbView = snackbar.view
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.YELLOW)
        snackbar.show()
    }

    open fun openSettingsDialog() {
        val EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key"
        val EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args"
        val EXTRA_SYSTEM_ALERT_WINDOW = "permission_settings"
        val bundle = Bundle()
        bundle.putString(EXTRA_FRAGMENT_ARG_KEY, EXTRA_SYSTEM_ALERT_WINDOW)
        val uri = Uri.fromParts("package", packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
            .putExtra(EXTRA_FRAGMENT_ARG_KEY, EXTRA_SYSTEM_ALERT_WINDOW)
            .putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle)
        startActivityForResult(intent, REQUEST_CODE_PERMISSION)
    }

    val localeDelegate: LocaleHelperActivityDelegate = LocaleHelperActivityDelegateImpl()

    override fun getDelegate() = localeDelegate.getAppCompatDelegate(super.getDelegate())

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localeDelegate.attachBaseContext(newBase))
    }

    override fun onResume() {
        super.onResume()
//        setThemeNN()
        localeDelegate.onResumed(this)
    }

    override fun onPause() {
        super.onPause()
        localeDelegate.onPaused()
    }

    override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        val context = super.createConfigurationContext(overrideConfiguration)
        return LocaleHelper.onAttach(context)
    }

    override fun getApplicationContext(): Context =
        localeDelegate.getApplicationContext(super.getApplicationContext())

    open fun updateLocale(locale: Locale) {
        localeDelegate.setLocale(this, locale)
    }


}