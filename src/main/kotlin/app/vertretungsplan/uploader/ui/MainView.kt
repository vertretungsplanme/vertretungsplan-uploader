package app.vertretungsplan.uploader.ui

import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import app.vertretungsplan.uploader.ui.style.MainStyleSheet
import app.vertretungsplan.uploader.ui.style.STYLE_BACKGROUND_COLOR
import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import app.vertretungsplan.uploader.ui.helpers.jfxButton
import app.vertretungsplan.uploader.ui.helpers.jfxTextfield
import app.vertretungsplan.uploader.ui.style.STYLE_PRIMARY_DARK_COLOR
import app.vertretungsplan.uploader.sync.SyncDaemon
import app.vertretungsplan.uploader.ui.helpers.jfxPasswordfield
import com.jfoenix.controls.JFXButton
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.image.Image
import javafx.scene.layout.BackgroundFill
import javafx.stage.DirectoryChooser
import tornadofx.*
import java.io.File

class MainView : View() {
    private var configStore = (app as VertretungsplanUploaderMain).configStore

    val sourceDirProperty = SimpleStringProperty(configStore.sourceDir)
    var sourceDir by sourceDirProperty

    val ftpServerField = jfxTextfield() {
        useMaxWidth = true
    }
    val ftpUserField = jfxTextfield() {
        useMaxWidth = true
    }
    val ftpPasswordField = jfxPasswordfield() {
        useMaxWidth = true
    }
    val ftpPortField = jfxTextfield() {
        useMaxWidth = true
    }

    private val contentBox = vbox {
        useMaxHeight = true

        style {
            alignment = Pos.CENTER
            backgroundColor += c(STYLE_BACKGROUND_COLOR)
            spacing = 20.px
            paddingLeft = 20.0
            paddingRight = 20.0
        }

        hbox {
            style {
                alignment = Pos.CENTER
            }

            label(messages["source_folder"]) {
                addClass(MainStyleSheet.settingLabel)
            }
            label(Bindings.`when`(sourceDirProperty.isNotNull).then(sourceDirProperty).otherwise(""))
            spacer {}
            jfxButton(messages["choose_folder"].toUpperCase()) {
                action {
                    val chooser = DirectoryChooser()
                    chooser.title = messages["source_folder"]
                    if (sourceDir != null) chooser.initialDirectory = File(sourceDir)
                    sourceDir = chooser.showDialog(currentWindow)?.absolutePath ?: sourceDir
                }
            }
        }

        hbox {
            useMaxWidth = true

            label(messages["ftp_server"]) {
                addClass(MainStyleSheet.settingLabel)
            }
            ftpServerField
        }

        hbox {
            useMaxWidth = true

            label(messages["ftp_user"]) {
                addClass(MainStyleSheet.settingLabel)
            }
            ftpUserField
        }

        hbox {
            useMaxWidth = true

            label(messages["ftp_password"]) {
                addClass(MainStyleSheet.settingLabel)
            }
            ftpPasswordField
        }

        hbox {
            useMaxWidth = true

            label(messages["ftp_port"]) {
                addClass(MainStyleSheet.settingLabel)
            }
            ftpPortField
        }

        jfxButton(messages["save"].toUpperCase()) {
            action {
                configStore.sourceDir = sourceDir
                configStore.ftpServer = ftpServerField.text
                configStore.ftpUser = ftpUserField.text
                configStore.ftpPassword = ftpPasswordField.text
                configStore.ftpPort = ftpPortField.text.toInt()
                SyncDaemon(app).run()
            }
        }

    }

    override val root: StackPane = stackpane {
        vbox {
            useMaxHeight = true

            gridpane {
                addClass(MainStyleSheet.toolBar)
                style {
                    minWidth = 100.percent
                }
                row {
                    hbox {
                        imageview(Image(resources.stream("vertretungsplan_logo.svg"), 0.0, 32.0, true, false))
                    }
                    spacer { }
                }
            }

            spacer { }
            this += contentBox
            spacer { }

        }
    }
}