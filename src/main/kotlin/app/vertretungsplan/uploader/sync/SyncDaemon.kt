package app.vertretungsplan.uploader.sync

import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import javafx.application.Application
import org.apache.commons.vfs2.tasks.SyncTask

class SyncDaemon(app: Application): Thread() {
    private val task = SyncTask()

    private var configStore = (app as VertretungsplanUploaderMain).configStore

    override fun run() {
        task.setDestDir("ftp://${configStore.ftpUser}:${configStore.ftpPassword}@${configStore
                .ftpServer}")
        task.setSrcDir(configStore.sourceDir)
        task.setSrcDirIsBase(true)
        task.execute()
    }
}