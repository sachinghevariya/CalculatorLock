package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager

import com.simplemobiletools.commons.models.FileDirItem

interface ItemOperationsListener {
    fun refreshFragment()

    fun deleteFiles(files: ArrayList<FileDirItem>)

    fun selectedPaths(paths: ArrayList<String>)

    fun setupDateTimeFormat()

    fun setupFontSize()

    fun toggleFilenameVisibility()

    fun columnCountChanged()

    fun finishActMode()
}
