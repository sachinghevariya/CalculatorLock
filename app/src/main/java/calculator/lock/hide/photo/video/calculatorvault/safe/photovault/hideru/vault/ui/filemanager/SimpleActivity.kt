package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager

import android.annotation.SuppressLint
import android.os.Environment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.R
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.hasPermission
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_STORAGE
import com.simplemobiletools.commons.helpers.isRPlus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class SimpleActivity : BaseSimpleActivity() {
    override fun getAppIconIDs() = arrayListOf(R.mipmap.ic_launcher)

    override fun getAppLauncherName() = getString(R.string.app_name)

    @SuppressLint("NewApi")
    fun hasStoragePermission(): Boolean {
        return if (isRPlus()) {
            Environment.isExternalStorageManager()
        } else {
            hasPermission(PERMISSION_WRITE_STORAGE)
        }
    }

}
