package app.vertretungsplan.uploader.sync

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import javafx.collections.ObservableList
import org.apache.commons.vfs2.FileObject
import tornadofx.observable

class FileInfo(val file: FileObject): RecursiveTreeObject<FileInfo>() {
    override fun getChildren(): ObservableList<FileInfo> {
        return if (file.isFolder) file.children.map { FileInfo(it) }.observable() else emptyList<FileInfo>().observable()
    }

    fun getName(): String? {
        return file.name.baseName
    }
}