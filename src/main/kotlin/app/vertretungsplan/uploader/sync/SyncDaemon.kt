package app.vertretungsplan.uploader.sync

import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import javafx.application.Application
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchKey


class SyncDaemon(app: Application): Thread() {
    private var configStore = (app as VertretungsplanUploaderMain).configStore

    init {
        isDaemon = true
    }

    private val FREQUENCY = 1000L

    override fun run() {
        val watcher = FileSystems.getDefault().newWatchService()
        val source = FileSystems.getDefault().getPath(configStore.sourceDir!!)
        val key = source.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)

        while (!isInterrupted) {
            val key: WatchKey
            try {
                key = watcher.take()
            } catch (x: InterruptedException) {
                return;
            }

            Thread.sleep(FREQUENCY);

            key.pollEvents()
            sync()

            // reset key to receive further events
            val valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private fun sync() {
        val destPath = "ftp://${configStore.ftpUser}:${configStore.ftpPassword}@${configStore
                .ftpServer}:${configStore.ftpPort}/"
        Sync(configStore.sourceDir!!, destPath).run()
    }
}