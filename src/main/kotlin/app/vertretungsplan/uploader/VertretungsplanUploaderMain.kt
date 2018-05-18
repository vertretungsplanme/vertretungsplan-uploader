package app.vertretungsplan.uploader

import app.vertretungsplan.uploader.sync.Sync
import app.vertretungsplan.uploader.sync.SyncDaemon
import app.vertretungsplan.uploader.ui.MainView
import app.vertretungsplan.uploader.ui.style.MainStyleSheet
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import net.harawata.appdirs.AppDirsFactory

class VertretungsplanUploaderMain : App(MainView::class, MainStyleSheet::class) {
    var stage: Stage? = null
    var daemon: SyncDaemon? = null

    private val appDirs = AppDirsFactory.getInstance()!!
    // Keep version argument at 1, we do not want new folders for every new version for now.
    private val dataDir = appDirs.getUserDataDir("uploader", "1", "vertretungsplanapp")
    val configStore = VertretungsplanUploaderPrefs(dataDir)

    override fun start(stage: Stage) {
        SvgImageLoaderFactory.install();
        this.stage = stage

        /*try {
            acquireLock(APP_ID, fun (message: String): String {
                fire(ConfigureEvent(message))
                return "ok"
            })
        } catch (e: AlreadyLockedException) {
            if (getInitUrl() != null) {
                JUnique.sendMessage(APP_ID, getInitUrl())
                System.exit(0)
            } else {
                val alert = Alert(AlertType.INFORMATION)
                alert.title = messages["running_already_title"]
                alert.headerText = messages["running_already_title"]
                alert.contentText = messages["running_already"]

                val buttonTypeCancel = ButtonType(messages["alert_cancel"], ButtonBar.ButtonData.OK_DONE)
                val buttonTypeIgnore = ButtonType(messages["alert_ignore"], ButtonBar.ButtonData.OTHER)
                alert.buttonTypes.setAll(buttonTypeIgnore, buttonTypeCancel)

                val res = alert.showAndWait()
                if (res.get() == buttonTypeCancel) {
                    System.exit(1)
                }
            }
        }*/

        //stage.icons += Image(VertretungsplanUploaderMain::class.java.getResourceAsStream("icon.png"))
        stage.minHeight = 100.0
        stage.minWidth = 800.0

        daemon = SyncDaemon(this)
        super.start(stage)

        val stylesheets = stage.scene.getStylesheets()
        stylesheets.addAll(resources["/css/jfoenix-fonts.css"], resources["/css/jfoenix-design.css"])

        daemon!!.start()
    }

    fun restartDaemon() {
        daemon!!.interrupt()
        daemon = SyncDaemon(this)
        daemon!!.start()
    }
}