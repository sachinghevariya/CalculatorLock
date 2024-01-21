package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils

import android.content.Context
import android.content.SharedPreferences

class MyPreferences constructor(context: Context) {
    init {
        sharedPreferences =
            context.getSharedPreferences(CommonClass.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }

    var passApp: String?
        get() = sharedPreferences.getString(CommonClass.PASS_APP, "passApp")
        set(passApp) {
            editor.putString(CommonClass.PASS_APP, passApp)
            editor.apply()
        }

    var securityAnswer: String?
        get() = sharedPreferences.getString(CommonClass.SECURITY_ANSWER, "")
        set(securityAnswer) {
            editor.putString(CommonClass.SECURITY_ANSWER, securityAnswer)
            editor.apply()
        }

    var securityQuestion: String?
        get() = sharedPreferences.getString(CommonClass.SECURITY_QUESTION, "")
        set(securityQuestion) {
            editor.putString(CommonClass.SECURITY_QUESTION, securityQuestion)
            editor.apply()
        }

    var themeSelected: String?
        get() = sharedPreferences.getString(CommonClass.THEME_SELECTED, "Theme0")
        set(themeSelected) {
            editor.putString(CommonClass.THEME_SELECTED, themeSelected)
            editor.apply()
        }

    var language: String?
        get() = sharedPreferences.getString(CommonClass.LANGUAGE, "en")
        set(lang) {
            editor.putString(CommonClass.LANGUAGE, lang)
            editor.apply()
        }

    var isPassSet: Boolean
        get() = sharedPreferences.getBoolean(CommonClass.IS_PASS_SET, false)
        set(isPassSet) {
            editor.putBoolean(CommonClass.IS_PASS_SET, isPassSet)
            editor.apply()
        }

    var isIntroShown: Boolean
        get() = sharedPreferences.getBoolean(CommonClass.IS_INTRO_SHOWN, false)
        set(isIntroShown) {
            editor.putBoolean(CommonClass.IS_INTRO_SHOWN, isIntroShown)
            editor.apply()
        }



    var lastVisitDate: String?
        get() = sharedPreferences.getString(CommonClass.KEY_LAST_VISIT_DATE, "")
        set(lastVisitDate) {
            editor.putString(CommonClass.KEY_LAST_VISIT_DATE, lastVisitDate)
            editor.apply()
        }


    var lastVisitDateReminder: String?
        get() = sharedPreferences.getString(CommonClass.KEY_LAST_VISIT_DATE_REMINDER, "")
        set(lastVisitDate) {
            editor.putString(CommonClass.KEY_LAST_VISIT_DATE_REMINDER, lastVisitDate)
            editor.apply()
        }

    var lastVisitDateStep: String?
        get() = sharedPreferences.getString(CommonClass.KEY_LAST_VISIT_DATE_STEP, "")
        set(lastVisitDate) {
            editor.putString(CommonClass.KEY_LAST_VISIT_DATE_STEP, lastVisitDate)
            editor.apply()
        }

    var lastVisitDateBmi: String?
        get() = sharedPreferences.getString(CommonClass.KEY_LAST_VISIT_DATE_BMI, "")
        set(lastVisitDate) {
            editor.putString(CommonClass.KEY_LAST_VISIT_DATE_BMI, lastVisitDate)
            editor.apply()
        }


    var adResponse: String?
        get() = sharedPreferences.getString(CommonClass.AD_RESPONSE, "")
        set(id) {
            editor.putString(CommonClass.AD_RESPONSE, id)
            editor.apply()
        }

    var bannerAdID: String?
        get() = sharedPreferences.getString(CommonClass.BANNER_ID, "")
        set(id) {
            editor.putString(CommonClass.BANNER_ID, id)
            editor.apply()
        }

    var interAdID: String?
        get() = sharedPreferences.getString(CommonClass.INTER_ID, "")
        set(id) {
            editor.putString(CommonClass.INTER_ID, id)
            editor.apply()
        }

    var nativeAdID: String?
        get() = sharedPreferences.getString(CommonClass.NATIVE_ID, "")
        set(id) {
            editor.putString(CommonClass.NATIVE_ID, id)
            editor.apply()
        }

    var appOpenAdID: String?
        get() = sharedPreferences.getString(CommonClass.APP_OPEN_ID, "")
        set(id) {
            editor.putString(CommonClass.APP_OPEN_ID, id)
            editor.apply()
        }

    var backInterAdID: String?
        get() = sharedPreferences.getString(CommonClass.BACK_INTER_ID, "")
        set(id) {
            editor.putString(CommonClass.BACK_INTER_ID, id)
            editor.apply()
        }

    var isPrivacyShown: Boolean
        get() = sharedPreferences.getBoolean(CommonClass.IS_PRIVACY_SHOWN, false)
        set(isPrivacyShown) {
            editor.putBoolean(CommonClass.IS_PRIVACY_SHOWN, isPrivacyShown)
            editor.apply()
        }

    var isPrivacyAccepted: Boolean
        get() = sharedPreferences.getBoolean(CommonClass.IS_PRIVACY_ACCEPTED, false)
        set(isPrivacyAccepted) {
            editor.putBoolean(CommonClass.IS_PRIVACY_ACCEPTED, isPrivacyAccepted)
            editor.apply()
        }
    var languageShown: Boolean
        get() = sharedPreferences.getBoolean(CommonClass.LANGUAGE_SHOWN, false)
        set(languageShown) {
            editor.putBoolean(CommonClass.LANGUAGE_SHOWN, languageShown)
            editor.apply()
        }

    companion object {
        private var myPreferences: MyPreferences? = null
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        fun getPreferences(context: Context): MyPreferences? {
            if (myPreferences == null) myPreferences = MyPreferences(context)
            return myPreferences
        }
    }
}