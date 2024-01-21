package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.DataRepository
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FileManagerViewModel @Inject constructor(
    @ApplicationContext val mContext: Context,
    private val mRepository: DataRepository
) : ViewModel() {
    val mergedTrashItems = MediatorLiveData<List<Any>>()

    private val _mediaItemsWithFolder =
        MutableStateFlow<HashMap<String, ArrayList<MediaItem>>>(HashMap())
    val mediaItemsWithFolder: StateFlow<HashMap<String, ArrayList<MediaItem>>> =
        _mediaItemsWithFolder

    val folderPhotoWithImagesFlow: LiveData<List<FolderItemWithMediaItems>> =
        mRepository.fetchPhotoFolderItemsWithImages()
    val folderVideoWithImagesFlow: LiveData<List<FolderItemWithMediaItems>> =
        mRepository.fetchVideoFolderItemsWithImages()
    val folderAudioWithAudioFlow: LiveData<List<FolderItemWithMediaItems>> =
        mRepository.fetchAudioFolderItemsWithAudios()
    val folderDocWithAudioFlow: LiveData<List<FolderItemWithMediaItems>> =
        mRepository.fetchDocumentFolderItemsWithAudios()

    /*    val folderPhotoItemsFlow: LiveData<List<FolderItem>> = mRepository.fetchPhotoFolderItems()
        val folderVideoItemsFlow: LiveData<List<FolderItem>> = mRepository.fetchVideoFolderItems()*/
    val allPhotoItemsFlow: LiveData<List<MediaItem>> = mRepository.fetchAllPhotoItems()
    val allVideoItemsFlow: LiveData<List<MediaItem>> = mRepository.fetchAllVideoItems()
    val allMediaForTrash: LiveData<List<MediaItem>> = mRepository.fetchAllTrashItems()
    val allAudioItemsFlow: LiveData<List<MediaItem>> = mRepository.fetchAllAudioItems()
    val allDocumentItemsFlow: LiveData<List<MediaItem>> = mRepository.fetchAllDocumentItems()
    val getCount: LiveData<Triple<Int, Int, Int>> = mRepository.getCount()


    private val _audioItemsWithFolder =
        MutableStateFlow<HashMap<String, ArrayList<MediaItem>>>(HashMap())
    val audioItemsWithFolder: StateFlow<HashMap<String, ArrayList<MediaItem>>> =
        _audioItemsWithFolder

    private val _documentItemsWithFolder =
        MutableStateFlow<HashMap<String, ArrayList<MediaItem>>>(HashMap())
    val documentItemsWithFolder: StateFlow<HashMap<String, ArrayList<MediaItem>>> =
        _documentItemsWithFolder


    fun fetchMediaItemsWithFolder() {
        viewModelScope.launch {
            val data = mRepository.fetchMediaItemsWithFolder()
            _mediaItemsWithFolder.value = data
        }
    }

    fun fetchAudioMediaItems() {
        viewModelScope.launch {
            val data = mRepository.fetchAudioMediaItems()
            _audioItemsWithFolder.value = data
        }
    }

    fun fetchDocumentMediaItems() {
        viewModelScope.launch {
            val data = mRepository.fetchDocumentMediaItems()
            _documentItemsWithFolder.value = data
        }
    }

    fun fetchMediaItemByAlbumImage(albumName: String): LiveData<List<MediaItem>> {
        return mRepository.fetchMediaItemByAlbum(albumName)
    }

    fun fetchMediaItemByAlbumVideo(albumName: String): LiveData<List<MediaItem>> {
        return mRepository.fetchMediaItemByAlbumVideo(albumName)
    }

    fun fetchMediaItemByAlbumAudio(albumName: String): LiveData<List<MediaItem>> {
        return mRepository.fetchMediaItemByAlbumAudio(albumName)
    }

    fun fetchMediaItemByAlbumDoc(albumName: String): LiveData<List<MediaItem>> {
        return mRepository.fetchMediaItemByAlbumDoc(albumName)
    }


    private val _mediaItemsWithFolderVideo =
        MutableStateFlow<HashMap<String, ArrayList<MediaItem>>>(HashMap())
    val mediaItemsWithFolderVideo: StateFlow<HashMap<String, ArrayList<MediaItem>>> =
        _mediaItemsWithFolderVideo

    fun fetchVideoMediaItemsWithFolder() {
        viewModelScope.launch {
            val data = mRepository.fetchVideoMediaItemsWithFolder()
            _mediaItemsWithFolderVideo.value = data
        }
    }

    fun addFolderToDb(folderName: String, mediaType: MediaType) {
        mRepository.addFolderToDb(folderName, mediaType)
    }

    fun addMediaItemToDb(mediaItem: MediaItem) {
        mRepository.addMediaItemToDb(mediaItem)
    }

    fun removeMediaItemToDb(mediaItem: MediaItem) {
        mRepository.removeMediaItemToDb(mediaItem)
    }

    fun removeMediaItemToDbSoft(mediaItem: MediaItem) {
        mRepository.removeMediaItemToDbSoft(mediaItem)
    }

    fun restoreMediaItemToDbSoft(mediaItem: MediaItem) {
        mRepository.restoreMediaItemToDbSoft(mediaItem)
    }

    fun updateMediaItemToDb(mediaItem: MediaItem) {
        mRepository.updateMediaItemToDb(mediaItem)
    }


    fun removeFolderFromDb(folderItem: FolderItem) {
        mRepository.removeFolderFromDb(folderItem)
    }


    suspend fun checkIsFolderExist(folderName: String): Boolean = withContext(Dispatchers.IO) {
        val folderItem = mRepository.checkIsFolderExist(folderName)
        return@withContext folderItem
    }


    //*********************--------Note-------*******************
    val allNotes: LiveData<List<NoteItem>> = mRepository.getAllNotes()
    val allTrashNotes: LiveData<List<NoteItem>> = mRepository.getAllTrashNotes()


    fun trashData() {
        mergedTrashItems.removeSource(allMediaForTrash)
        mergedTrashItems.removeSource(allTrashNotes)
        mergedTrashItems.addSource(allMediaForTrash) { mediaItems ->
            val currentNotes = mergedTrashItems.value?.filterIsInstance<NoteItem>() ?: emptyList()
            val mergedList = mediaItems + currentNotes
            mergedTrashItems.value = mergedList
        }

        mergedTrashItems.addSource(allTrashNotes) { noteItems ->
            val currentMediaItems =
                mergedTrashItems.value?.filterIsInstance<MediaItem>() ?: emptyList()
            val mergedList = currentMediaItems + noteItems
            mergedTrashItems.value = mergedList
        }
    }

    fun addNoteToDb(noteItem: NoteItem) {
        viewModelScope.launch {
            mRepository.addNoteToDb(noteItem)
        }
    }

    fun updateNoteToDb(noteItem: NoteItem) {
        viewModelScope.launch {
            mRepository.updateNoteToDb(noteItem)
        }
    }

    fun removeNoteItemToDb(noteItem: NoteItem) {
        viewModelScope.launch {
            mRepository.removeNoteItemToDb(noteItem)
        }
    }

    fun removeNoteItemToDbSoft(noteItem: NoteItem) {
        viewModelScope.launch {
            mRepository.removeNoteItemToDbSoft(noteItem)
        }
    }

    fun restoreNoteItemToDbSoft(noteItem: NoteItem) {
        mRepository.restoreNoteItemToDbSoft(noteItem)
    }

}