package app.vertretungsplan.uploader

import app.vertretungsplan.uploader.sync.SyncDaemon
import app.vertretungsplan.uploader.ui.MainView
import app.vertretungsplan.uploader.ui.style.MainStyleSheet
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import it.sauronsoftware.junique.AlreadyLockedException
import it.sauronsoftware.junique.JUnique
import it.sauronsoftware.junique.JUnique.acquireLock
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.WindowEvent
import net.harawata.appdirs.AppDirsFactory
import tornadofx.*
import java.awt.*
import java.awt.event.ActionListener
import java.util.*
import javax.imageio.ImageIO


val APP_ID = "app.vertretungsplan.uploader"

class VertretungsplanUploaderMain : App(MainView::class, MainStyleSheet::class) {
    var stage: Stage? = null
    var daemon: SyncDaemon? = null
    val trayIcon: TrayIcon


    private val _messages: SimpleObjectProperty<ResourceBundle> = object : SimpleObjectProperty<ResourceBundle>() {
        override fun get(): ResourceBundle? {
            if (super.get() == null) {
                try {
                    val bundle = ResourceBundle.getBundle(this@VertretungsplanUploaderMain.javaClass.name, FX.locale,
                            this@VertretungsplanUploaderMain.javaClass.classLoader, FXResourceBundleControl)
                    (bundle as? FXPropertyResourceBundle)?.inheritFromGlobal()
                    set(bundle)
                } catch (ex: Exception) {
                    FX.log.fine("No Messages found for ${javaClass.name} in locale ${FX.locale}, using global bundle")
                    set(FX.messages)
                }
            }
            return super.get()
        }
    }

    var messages: ResourceBundle
        get() = _messages.get()
        set(value) = _messages.set(value)

    init {
        SvgImageLoaderFactory.install();
        // get the SystemTray instance
        val tray = SystemTray.getSystemTray()
        val trayIconSize = tray.trayIconSize
        // load an image
        var image = ImageIO.read(resources.stream("vertretungsplan_icon_tray.png"))

        // create a action listener to listen for default action executed on the tray icon
        val closeListener = ActionListener { System.exit(0) }

        val showListener = ActionListener {
            Platform.runLater {
                stage!!.show()
                stage!!.isIconified = false
            }
        }
        // create a popup menu
        val popup = PopupMenu()

        val showItem = MenuItem(messages["settings"])
        showItem.addActionListener(showListener)
        popup.add(showItem)

        val closeItem = MenuItem(messages["exit"])
        closeItem.addActionListener(closeListener)
        popup.add(closeItem)
        /// ... add other items
        // construct a TrayIcon
        trayIcon = TrayIcon(image!!, messages["app_name"], popup)
        // set the TrayIcon properties
        trayIcon.addActionListener(showListener)
        // ...
        // add the tray image
        try {
            tray.add(trayIcon)
        } catch (e: AWTException) {
            System.err.println(e)
        }
    }

    private val appDirs = AppDirsFactory.getInstance()!!
    // Keep version argument at 1, we do not want new folders for every new version for now.
    private val dataDir = appDirs.getUserDataDir("uploader", "1", "vertretungsplanapp")
    val configStore = VertretungsplanUploaderPrefs(dataDir)

    override fun start(stage: Stage) {
        this.stage = stage

        setupCloseToTray(stage)
        ensureSingleInstance(stage)

        stage.icons += Image(resources.stream("vertretungsplan_icon.svg"))
        stage.minHeight = 100.0
        stage.minWidth = 500.0

        daemon = SyncDaemon(this)
        super.start(stage)

        val stylesheets = stage.scene.getStylesheets()
        stylesheets.addAll(resources["/css/jfoenix-fonts.css"], resources["/css/jfoenix-design.css"])

        daemon!!.start()
    }

    private fun setupCloseToTray(stage: Stage) {
        if (SystemTray.isSupported()) {
            stage.onCloseRequest = EventHandler<WindowEvent> {
                Platform.runLater {
                    if (SystemTray.isSupported()) {
                        stage.hide()
                        //showProgramIsMinimizedMsg()
                    } else {
                        System.exit(0)
                    }
                }
            }

        }
        Platform.setImplicitExit(false);
    }

    private fun ensureSingleInstance(stage: Stage) {
        try {
            acquireLock(APP_ID, fun(message: String): String {
                Platform.runLater {
                    stage.show()
                    stage.isIconified = false
                }
                return "ok"
            })
        } catch (e: AlreadyLockedException) {
            JUnique.sendMessage(APP_ID, "")
            System.exit(0)
        }
    }

    fun restartDaemon() {
        daemon!!.interrupt()
        daemon = SyncDaemon(this)
        daemon!!.start()
    }
}