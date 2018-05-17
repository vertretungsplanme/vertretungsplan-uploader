package app.vertretungsplan.uploader.sync

import org.apache.commons.vfs2.*
import java.util.*
import java.util.logging.Logger


class Sync(val srcPath: String, val destPath: String, val delete: Boolean = true, val
compareSize: Boolean = false, val compareDate: Boolean = true, val compareDateNewer: Boolean =
        true, val preserveLastModified: Boolean = true) {
    protected var cntFiles = 0
    protected var cntSyncFiles = 0
    protected var cntDirs = 0
    protected var cntSyncDirs = 0
    protected var cntRemoved = 0
    protected val logger = Logger.getLogger("Sync")

    fun run() {
        val man = VFS.getManager()
        val src = man.resolveFile(srcPath)
        val dest = man.resolveFile(destPath)

        if (src.getType().equals(FileType.FILE)) {
            if (dest.getType().equals(FileType.FOLDER)) {
                val imaginaryDest = dest.resolveFile(src.getName().getBaseName())
                syncFiles(src, imaginaryDest)
            } else {
                syncFiles(src, dest)
            }
        } else if (src.getType().equals(FileType.FOLDER)) {
            if (dest.getType().equals(FileType.FILE)) {
                throw IllegalArgumentException("You cannot synchronize a folder with a file")
            } else {
                // do not count the starting dir
                cntDirs--
                syncDirs(src, dest)
            }
        }
    }

    fun syncFiles(srcFile: FileObject, destFile: FileObject) {
        cntFiles++
        if (!areSame(srcFile, destFile)) {
            syncFileAction(srcFile, destFile)
            cntSyncFiles++
        }
    }

    fun syncDirs(srcDir: FileObject, destDir: FileObject) {
        val destChildren = LinkedList<FileObject>()
        cntDirs++
        if (!destDir.exists()) {
            syncDirAction(srcDir, destDir)
            cntSyncDirs++
        } else {
            val tmpDestChildren = destDir.getChildren()
            //create list of destChildren
            for (i in tmpDestChildren.indices) {
                destChildren.add(tmpDestChildren[i])
            }
        }

        //spider through the children
        val srcChildren = srcDir.getChildren()

        for (i in srcChildren.indices) {
            val srcChild = srcChildren[i]
            val destChild = destDir.resolveFile(srcChild.getName().getBaseName(), NameScope.CHILD)
            destChildren.remove(destChild)

            //if delete, remove destChild in case of type conflict
            if (delete) {
                if (destChild.exists() && !srcChild.getType().equals(destChild.getType())) {
                    cntRemoved += typeConflictAction(srcChild, destChild)
                }
            }

            //both are files (dest can be imaginary)
            if (!srcChild.getType().equals(FileType.FOLDER) && !destChild.getType().equals(FileType.FOLDER)) {
                syncFiles(srcChild, destChild)
            }

            //both are folders (dest can be imaginary)
            if (!srcChild.getType().equals(FileType.FILE) && !destChild.getType().equals(FileType.FILE)) {
                syncDirs(srcChild, destChild)
            }

            //when done processing, release reference to child
            srcChildren[i] = null

        }

        if (delete) {
            for (i in destChildren.indices) {
                val remainingChild = destChildren.get(i) as FileObject
                cntRemoved += remainingChildAction(remainingChild)
            }
        }
    }
    
    protected fun remainingChildAction(remainingChild: FileObject): Int {
        var remaining  = remainingChild.delete(Selectors.SELECT_ALL)
        logger.info("Removed " + toString(remainingChild))

        return remaining
    }

    private fun toString(fileObject: FileObject): String {
        return fileObject.publicURIString
    }

    protected fun typeConflictAction(srcChild: FileObject, destChild: FileObject): Int {

        var removed = 0

        if ((srcChild.type == FileType.FILE) and (destChild.type == FileType.FOLDER)) {
            removed = destChild.delete(Selectors.SELECT_ALL)
            logger.info("Removed folder " + toString(destChild) + " because of a type conflict")
        } else if (srcChild.type == FileType.FOLDER && destChild.type == FileType.FILE) {
            destChild.delete()
            removed = 1

            logger.info("Removed file " + toString(destChild) + " because of a type conflict")
        }
        return removed
    }
    
    protected fun syncDirAction(srcDir: FileObject, destDir: FileObject) {
        //create the destDir
        destDir.copyFrom(srcDir, Selectors.SELECT_SELF)

        if (preserveLastModified &&
                srcDir.fileSystem.hasCapability(Capability.GET_LAST_MODIFIED) &&
                destDir.fileSystem.hasCapability(Capability.SET_LAST_MODIFIED_FOLDER)) {
            destDir.content.lastModifiedTime = srcDir.content.lastModifiedTime
        }
        logger.info("Copied directory " + toString(srcDir) + " to " + toString(destDir))
    }
    
    protected fun syncFileAction(srcFile: FileObject, destFile: FileObject) {
        destFile.copyFrom(srcFile, Selectors.SELECT_SELF)

        if (preserveLastModified &&
                srcFile.fileSystem.hasCapability(Capability.GET_LAST_MODIFIED) &&
                destFile.fileSystem.hasCapability(Capability.SET_LAST_MODIFIED_FILE)) {
            destFile.content.lastModifiedTime = srcFile.content.lastModifiedTime
        }
        logger.info("Copied file " + toString(srcFile) + " to " + toString(destFile))
    }
    
    protected fun areSame(fileA: FileObject, fileB: FileObject): Boolean {

        if (!fileB.exists()) {
            return false
        }

        var sameDate = true
        if (compareDate) {
            if (compareDateNewer) {
                sameDate = fileA.content.lastModifiedTime <= fileB.content.lastModifiedTime
            } else {
                sameDate = fileA.content.lastModifiedTime == fileB.content.lastModifiedTime
            }

        }

        var sameSize = true
        if (compareSize) {
            sameSize = fileA.content.size == fileB.content.size
        }

        /*var sameMd5 = true
        if (compareMd5) {
            val md5A = md5Helper.calculateMd5(fileA)
            val md5B = md5Helper.calculateMd5(fileB)
            sameMd5 = md5A == md5B
        }*/

        return sameSize && sameDate //&& sameMd5
    }
}
