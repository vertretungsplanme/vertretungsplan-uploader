package app.vertretungsplan.uploader.sync

import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import javafx.application.Application
import org.apache.commons.vfs2.tasks.SyncTask

class SyncDaemon(app: Application): Thread() {
    private var configStore = (app as VertretungsplanUploaderMain).configStore

    override fun run() {
        val destPath = "ftp://${configStore.ftpUser}:${configStore.ftpPassword}@${configStore
                .ftpServer}:${configStore.ftpPort}/"
        Sync(configStore.sourceDir!!, destPath).run()
    }
}