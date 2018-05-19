package app.vertretungsplan.uploader.sync

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import org.apache.commons.vfs2.FileObject
import tornadofx.observable

class FileInfo(val file: FileObject): RecursiveTreeObject<FileInfo>() {
    init {
        if (file.isFolder) {
            this.children = file.children.map { FileInfo(it) }.observable()
        }
    }

    override fun toString(): String {
        return file.name.baseName
    }
}