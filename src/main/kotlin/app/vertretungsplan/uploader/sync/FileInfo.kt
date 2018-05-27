package app.vertretungsplan.uploader.sync

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import javafx.collections.ObservableList
import org.apache.commons.vfs2.FileObject
import tornadofx.observable

class FileInfo(val file: FileObject, val showPath: Boolean = false): RecursiveTreeObject<FileInfo>() {
    override fun getChildren(): ObservableList<FileInfo> {
        return if (file.isFolder) file.children.map { FileInfo(it) }.observable() else emptyList<FileInfo>().observable()
    }

    fun getName(): String? {
        return if (showPath) file.name.path else file.name.baseName
    }
}