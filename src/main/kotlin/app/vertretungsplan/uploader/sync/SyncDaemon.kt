package app.vertretungsplan.uploader.sync

import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import com.sun.nio.file.ExtendedWatchEventModifier
import javafx.application.Application
import java.nio.file.FileSystems
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.WatchKey


class SyncDaemon(app: Application, var callback: Sync.Callback? = null): Thread() {
    private var configStore = (app as VertretungsplanUploaderMain).configStore

    init {
        isDaemon = true
    }

    private val FREQUENCY = 1000L

    override fun run() {
        if (configStore.ftpUser != null && configStore.ftpPassword != null && configStore
                        .ftpServer != null) {
            val watcher = FileSystems.getDefault().newWatchService()
            val source = FileSystems.getDefault().getPath(configStore.sourceDir!!)
            source.register(watcher, arrayOf(ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY), ExtendedWatchEventModifier.FILE_TREE)

            sync()

            while (!isInterrupted) {
                val key: WatchKey
                try {
                    key = watcher.take()
                    Thread.sleep(FREQUENCY);
                } catch (x: InterruptedException) {
                    return;
                }

                key.pollEvents()
                sync()

                // reset key to receive further events
                val valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        }
    }

    private fun sync() {
        try {
            val destPath = "ftp://${configStore.ftpUser}:${configStore.ftpPassword}@${configStore
                    .ftpServer}:${configStore.ftpPort}/"
            if (callback != null) {
                Sync(configStore.sourceDir!!, destPath, callback = callback!!).run()
            } else {
                Sync(configStore.sourceDir!!, destPath).run()
            }
        } catch (e: Throwable) {
            Thread.sleep(FREQUENCY);
            sync()
        }
    }
}