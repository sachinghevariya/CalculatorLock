package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils

import android.app.ActivityManager
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

var isClickNotification = false

class CommonClass {
    companion object {
        const val AD_URL = "https://ht.askforad.com/"
        const val CHANNEL_ID = "ALARM_SERVICE_CHANNEL_NEW"
        const val SHARED_PREFERENCES_NAME = "my_preference"
        const val PASS_APP = "passApp"
        const val SECURITY_ANSWER = "securityAnswer"
        const val SECURITY_QUESTION = "securityQuestion"
        const val THEME_SELECTED = "themeSelected"
        const val LANGUAGE = "language"
        const val IS_PASS_SET = "isPassSet"
        const val IS_INTRO_SHOWN = "isIntroShown"
        const val IS_PRIVACY_SHOWN = "isPrivacyShown"
        const val IS_PRIVACY_ACCEPTED = "isPrivacyAccepted"
        const val LANGUAGE_SHOWN = "languageShown"
        const val KEY_LAST_VISIT_DATE = "last_visit_date"
        const val KEY_LAST_VISIT_DATE_REMINDER = "last_visit_date_reminder"
        const val KEY_LAST_VISIT_DATE_STEP = "last_visit_date_step"
        const val KEY_LAST_VISIT_DATE_BMI = "last_visit_date_bmi"
        const val IS_REMINDER_AD_AVAILABLE = "isReminderAdAvailable"
        const val IS_BMI_AD_AVAILABLE = "isBmiAdAvailable"
        const val IS_STEP_AD_AVAILABLE = "isStepAdAvailable"

        const val DEFAULT_GOAL = "default_goal"
        const val START_FOREGROUND = "start_foreground"
        const val STOP_FOREGROUND = "stop_foreground"
        const val RESET_COUNT = "reset_count"
        const val STOP_SAVE_COUNT = "stop_save_count"
        const val AD_RESPONSE = "ad_response"
        const val BANNER_ID = "banner_id"
        const val INTER_ID = "inter_id"
        const val NATIVE_ID = "native_id"
        const val APP_OPEN_ID = "app_open_id"
        const val BACK_INTER_ID = "back_inter_id"
        var pagerList:ArrayList<MediaItem> = arrayListOf()
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun showBottomSheet(activity: AppCompatActivity, bottomSheetFragment: BottomSheetDialogFragment) {
    bottomSheetFragment.show(activity.supportFragmentManager, bottomSheetFragment.tag)
//    bottomSheetFragment.dialog?.window?.decorView?.post {
//        val contentView = bottomSheetFragment.dialog?.window?.decorView
//        if (contentView is NestedScrollView) {
//            contentView.fullScroll(View.FOCUS_DOWN)
//        }
//    }
}

fun showMyDialog(activity: AppCompatActivity, bottomSheetFragment: DialogFragment) {
    bottomSheetFragment.show(activity.supportFragmentManager, bottomSheetFragment.tag)
}

fun hideDialog(activity: AppCompatActivity, bottomSheetFragment: DialogFragment) {
    bottomSheetFragment.dismiss()
}


fun generateRandomInt(): Int {
    return Random.nextInt(2, 999999999 + 1) // +1 to include the upper bound
}


fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.inVisible() {
    visibility = View.INVISIBLE
}

fun startToClick() {
    isClickNotification = true
}

fun stopToClick() {
    isClickNotification = false
}

fun isAppRunningInBackground(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    val runningAppProcesses = activityManager?.runningAppProcesses

    if (runningAppProcesses != null) {
        for (processInfo in runningAppProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                processInfo.processName == context.packageName
            ) {
                return false
            }
        }
    }
    return true
}

//val Context.navigationBarHeight: Int
//    get() {
//        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//
//        return if (Build.VERSION.SDK_INT >= 30) {
//            windowManager
//                .currentWindowMetrics
//                .windowInsets
//                .getInsets(WindowInsets.Type.navigationBars())
//                .bottom
//
//        } else {
//            val currentDisplay = try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    display
//                } else {
//                    windowManager.defaultDisplay
//                }
//            } catch (e: NoSuchMethodError) {
//                windowManager.defaultDisplay
//            }
//
//            val appUsableSize = Point()
//            val realScreenSize = Point()
//            currentDisplay?.apply {
//                getSize(appUsableSize)
//                getRealSize(realScreenSize)
//            }
//
//            // navigation bar on the side
//            if (appUsableSize.x < realScreenSize.x) {
//                return realScreenSize.x - appUsableSize.x
//            }
//
//            // navigation bar at the bottom
//            return if (appUsableSize.y < realScreenSize.y) {
//                realScreenSize.y - appUsableSize.y
//            } else 0
//        }
//    }


fun getCurrentTimeInAMPM(currentTimeMillis:Long): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.US)
    val currentDate = Date(currentTimeMillis)
    return sdf.format(currentDate)
}