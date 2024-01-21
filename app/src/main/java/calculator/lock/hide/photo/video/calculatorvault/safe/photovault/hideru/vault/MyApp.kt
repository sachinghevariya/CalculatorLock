package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.lifecycle.ProcessLifecycleOwner
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.adsModule.AppOpenAdsHelper
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.room.AppDatabase
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.CommonClass.Companion.CHANNEL_ID
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.deleteFileWithCursor
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.zeugmasolutions.localehelper.LocaleHelper
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import plugin.adsdk.service.AdsUtility
import plugin.adsdk.service.AppOpenManager
import plugin.adsdk.service.BaseApp
import plugin.adsdk.service.api.ListModel
import plugin.adsdk.service.utils.PurchaseHandler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    private val localeAppDelegate = LocaleHelperApplicationDelegate()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localeAppDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAppDelegate.onConfigurationChanged(this)
    }

    override fun getApplicationContext(): Context =
        LocaleHelper.onAttach(super.getApplicationContext())

    companion object {
        private var mInstance: MyApp? = null
        var instance: AppOpenAdsHelper? = null

        @Synchronized
        fun getInstance(): MyApp? {
            return mInstance
        }

        @Synchronized
        fun getInstanceAppOpen(): AppOpenAdsHelper? {
            return instance!!
        }
    }

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        val testDeviceIds = listOf(
            "EF7B9A70EBF476FB4E4507F7ED7FAA92",
            "B315DAF5E88021C1B92FD54D8044F312",
            "E1963467428971225BC5DF2EFF53D991"
        )
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        getConfig()
        MobileAds.initialize(this)
        AppOpenManager.init(this)
        PurchaseHandler.init(this)
        instance = AppOpenAdsHelper()
        registerActivityLifecycleCallbacks(instance)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(instance!!)
        ProcessLifecycleOwner.get().lifecycle.addObserver(instance!!)
        checkAndInsertFolders()
        checkIsNeedToDelete()
    }

    private fun getConfig() {
        if (AdsUtility.config != null) {
            return
        }
        AdsUtility.config = ListModel()
//        if (PurchaseHandler.hasPurchased(this)) {
//            AdsUtility.config.migrateToNoAds()
//        }
        AdsUtility.config.packageName =
            "calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault"
        AdsUtility.config.initialAppOpen = false
        AdsUtility.config.blockInitialAppOpen = true
        AdsUtility.config.activityCount = 0
        AdsUtility.config.adOnBack = true
        AdsUtility.config.privacyPolicyUrl =
            "https://sites.google.com/view/calculator-lock-hide-app-photo/home"
        AdsUtility.config.preloadNative = false
        AdsUtility.config.preloadBanner = true
        AdsUtility.config.preloadInterstitial = true
        AdsUtility.config.base64InAppKey = ""
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun checkAndInsertFolders() {
        CoroutineScope(Dispatchers.IO).launch {
            val folderItemDao = appDatabase.getFolderItemDao()
            val folderNamesToCheck = listOf("My Photos", "My Videos", "My Audios", "My Documents")
            val existingFolders = folderItemDao.getFoldersByName(folderNamesToCheck)
            val foldersToInsert = folderNamesToCheck.filter { name ->
                existingFolders.none { it.folderName == name }
            }
            if (foldersToInsert.isNotEmpty()) {
                val newFolderItems = foldersToInsert.map { folderName ->
                    val mediaType = when (folderName) {
                        "My Photos" -> {
                            MediaType.IMAGE
                        }

                        "My Videos" -> {
                            MediaType.VIDEO
                        }

                        "My Audios" -> {
                            MediaType.AUDIO
                        }

                        "My Documents" -> {
                            MediaType.DOC
                        }

                        else -> {
                            MediaType.IMAGE
                        }
                    }
                    val id = when (folderName) {
                        "My Photos" -> {
                            0
                        }

                        "My Videos" -> {
                            1
                        }

                        "My Audios" -> {
                            2
                        }

                        "My Documents" -> {
                            3
                        }

                        else -> {
                            0
                        }
                    }
                    FolderItem(id, folderName, mediaType)
                }
                folderItemDao.insertFolderItems(*newFolderItems.toTypedArray())

            }
        }
    }

    private fun checkIsNeedToDelete() {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val itemsToDeleteDeferred = CompletableDeferred<List<MediaItem>>()
        val itemsToDelete = mutableListOf<MediaItem>()

        CoroutineScope(Dispatchers.IO).launch {
            val mediaItemDao = appDatabase.getMedaItemDao()
            val list = mediaItemDao.getTrashData(1)
            list.forEach { mediaItem ->
                if (mediaItem.deleteDate.isNotEmpty()) {
                    val deleteDate = dateFormat.parse(mediaItem.deleteDate)
                    val daysDifference = daysBetweenDates(deleteDate, currentDate)
                    if (daysDifference >= 15) {
                        itemsToDelete.add(mediaItem)
                    }
                }
            }
            itemsToDeleteDeferred.complete(itemsToDelete) // Complete the deferred with the result
            val result: List<MediaItem> = itemsToDeleteDeferred.await()
            result.map { mediaItem ->
                async(Dispatchers.IO) {
                    deleteFileWithCursor(this@MyApp, mediaItem) { _ ->
                        mediaItemDao.delete(mediaItem)
                    }
                }
            }
        }
    }

    private fun daysBetweenDates(startDate: Date, endDate: Date): Long {
        val difference = endDate.time - startDate.time
        return difference / (1000 * 60 * 60 * 24)
    }

}