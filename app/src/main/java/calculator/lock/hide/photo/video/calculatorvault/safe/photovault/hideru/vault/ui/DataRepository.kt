package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.room.AppDatabase
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.fetchAudioMediaItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.fetchDocumentMediaItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.fetchMediaFolderItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.fetchVideoMediaFolderItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils.generateRandomInt
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


class DataRepository @Inject constructor(
    @ApplicationContext val mContext: Context,
    private val mAppDb: AppDatabase
) {

    suspend fun fetchAudioMediaItems(): HashMap<String, ArrayList<MediaItem>> {
        return fetchAudioMediaItems(mContext)
    }

    suspend fun fetchDocumentMediaItems(): HashMap<String, ArrayList<MediaItem>> {
        return fetchDocumentMediaItems(mContext)
    }

    suspend fun fetchMediaItemsWithFolder(): HashMap<String, ArrayList<MediaItem>> {
        return fetchMediaFolderItems(mContext)
    }

    suspend fun fetchVideoMediaItemsWithFolder(): HashMap<String, ArrayList<MediaItem>> {
        return fetchVideoMediaFolderItems(mContext)
    }

    fun fetchAllTrashItems(): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().getAllTrashData(1)
    }

    fun fetchAllPhotoItems(): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().getAllData(MediaType.IMAGE)
    }

    fun fetchAllVideoItems(): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().getAllData(MediaType.VIDEO)
    }

    fun fetchAllAudioItems(): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().getAllData(MediaType.AUDIO)
    }

    fun fetchAllDocumentItems(): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().getAllData(MediaType.DOC)
    }

    fun fetchPhotoFolderItems(): LiveData<List<FolderItem>> {
        return mAppDb.getFolderItemDao().getFoldersByType(MediaType.IMAGE)
    }

    fun fetchMediaItemByAlbum(albumName: String): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().fetchMediaItemByAlbum(albumName, MediaType.IMAGE)
    }

    fun fetchMediaItemByAlbumVideo(albumName: String): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().fetchMediaItemByAlbum(albumName, MediaType.VIDEO)
    }

    fun fetchMediaItemByAlbumAudio(albumName: String): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().fetchMediaItemByAlbum(albumName, MediaType.AUDIO)
    }

    fun fetchMediaItemByAlbumDoc(albumName: String): LiveData<List<MediaItem>> {
        return mAppDb.getMedaItemDao().fetchMediaItemByAlbum(albumName, MediaType.DOC)
    }

    fun fetchPhotoFolderItemsWithImages(): LiveData<List<FolderItemWithMediaItems>> {
        return mAppDb.getFolderItemDao().getFoldersWithMediaItems(MediaType.IMAGE)
    }

    fun fetchVideoFolderItemsWithImages(): LiveData<List<FolderItemWithMediaItems>> {
        return mAppDb.getFolderItemDao().getFoldersWithMediaItems(MediaType.VIDEO)
    }

    fun fetchAudioFolderItemsWithAudios(): LiveData<List<FolderItemWithMediaItems>> {
        return mAppDb.getFolderItemDao().getFoldersWithMediaItems(MediaType.AUDIO)
    }

    fun fetchDocumentFolderItemsWithAudios(): LiveData<List<FolderItemWithMediaItems>> {
        return mAppDb.getFolderItemDao().getFoldersWithMediaItems(MediaType.DOC)
    }

    fun fetchVideoFolderItems(): LiveData<List<FolderItem>> {
        return mAppDb.getFolderItemDao().getFoldersByType(MediaType.VIDEO)
    }

    fun addFolderToDb(folderName: String, mediaType: MediaType) {
        val item = FolderItem(generateRandomInt(), folderName, mediaType)
        mAppDb.getFolderItemDao().insert(item)
    }

    fun addMediaItemToDb(mediaItem: MediaItem) {
        mAppDb.getMedaItemDao().insert(mediaItem)
    }

    fun removeMediaItemToDb(mediaItem: MediaItem) {
        mAppDb.getMedaItemDao().delete(mediaItem)
    }

    fun removeMediaItemToDbSoft(mediaItem: MediaItem) {
        mediaItem.isTrash = 1
        mediaItem.deleteDate = getCurrentDateInDDMMYYYYFormat()
        mAppDb.getMedaItemDao().update(mediaItem)
    }

    fun restoreMediaItemToDbSoft(mediaItem: MediaItem) {
        mediaItem.isTrash = 0
        mediaItem.deleteDate = ""
        mAppDb.getMedaItemDao().update(mediaItem)
    }

    fun updateMediaItemToDb(mediaItem: MediaItem) {
        mAppDb.getMedaItemDao().update(mediaItem)
    }

    fun removeFolderFromDb(folderItem: FolderItem) {
        mAppDb.getFolderItemDao().delete(folderItem)
    }

    private fun getCurrentDateInDDMMYYYYFormat(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        return dateFormat.format(date)
    }

    fun checkIsFolderExist(folderName: String): Boolean {
        val folderItem = mAppDb.getFolderItemDao().getFolderByName(folderName)
        if (folderItem != null) {
            return true
        }
        return false
    }


    //*********************--------Note-------*******************

    fun getAllNotes(): LiveData<List<NoteItem>> {
        return mAppDb.getNotesItemDao().getAllNotesData()
    }

    fun getAllTrashNotes(): LiveData<List<NoteItem>> {
        return mAppDb.getNotesItemDao().getAllTrashNotes()
    }

    fun addNoteToDb(noteItem: NoteItem) {
        mAppDb.getNotesItemDao().insert(noteItem)
    }

    fun updateNoteToDb(noteItem: NoteItem) {
        mAppDb.getNotesItemDao().update(noteItem)
    }

    fun removeNoteItemToDb(noteItem: NoteItem) {
        mAppDb.getNotesItemDao().delete(noteItem)
    }

    fun removeNoteItemToDbSoft(noteItem: NoteItem) {
        noteItem.isTrash = 1
        noteItem.deleteDate = getCurrentDateInDDMMYYYYFormat()
        mAppDb.getNotesItemDao().update(noteItem)
    }

    fun restoreNoteItemToDbSoft(noteItem: NoteItem) {
        noteItem.isTrash = 0
        noteItem.deleteDate = ""
        mAppDb.getNotesItemDao().update(noteItem)
    }


    fun getCount(): LiveData<Triple<Int, Int, Int>> {
        val resultLiveData = MediatorLiveData<Triple<Int, Int, Int>>()
        val notesCountLiveData = mAppDb.getNotesItemDao().getNotesCount()
        val audioCountLiveData = mAppDb.getMedaItemDao().getMediaCount(MediaType.AUDIO)

        resultLiveData.removeSource(notesCountLiveData)
        resultLiveData.removeSource(audioCountLiveData)

        resultLiveData.addSource(notesCountLiveData) { notesCount ->
            resultLiveData.value =
                Triple(notesCount, audioCountLiveData.value ?: 0, 0)
        }

        resultLiveData.addSource(audioCountLiveData) { audioCount ->
            resultLiveData.value =
                Triple(notesCountLiveData.value ?: 0, audioCount, 0)
        }

        return resultLiveData
    }
}