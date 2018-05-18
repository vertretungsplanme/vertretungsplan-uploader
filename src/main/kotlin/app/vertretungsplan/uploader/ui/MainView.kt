package app.vertretungsplan.uploader.ui

import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import app.vertretungsplan.uploader.ui.style.MainStyleSheet
import app.vertretungsplan.uploader.ui.style.STYLE_BACKGROUND_COLOR
import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import app.vertretungsplan.uploader.ui.helpers.jfxButton
import app.vertretungsplan.uploader.ui.helpers.jfxTextfield
import app.vertretungsplan.uploader.sync.SyncDaemon
import app.vertretungsplan.uploader.ui.helpers.jfxPasswordfield
import com.jfoenix.controls.JFXTextField
import com.jfoenix.validation.NumberValidator
import com.jfoenix.validation.RequiredFieldValidator
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import javafx.stage.DirectoryChooser
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import java.io.File

class MainView : View() {
    private var configStore = (app as VertretungsplanUploaderMain).configStore

    val sourceDirProperty = SimpleStringProperty(configStore.sourceDir)
    var sourceDir by sourceDirProperty

    val ftpServerProperty = SimpleStringProperty(configStore.ftpServer)
    var ftpServer by ftpServerProperty

    val ftpUserProperty = SimpleStringProperty(configStore.ftpUser)
    var ftpUser by ftpUserProperty

    val ftpPasswordProperty = SimpleStringProperty(configStore.ftpPassword)
    var ftpPassword by ftpPasswordProperty

    val ftpPortProperty = SimpleIntegerProperty(configStore.ftpPort)
    var ftpPort by ftpPortProperty

    private val contentBox = vbox {
        useMaxHeight = true

        style {
            alignment = Pos.CENTER
            backgroundColor += c(STYLE_BACKGROUND_COLOR)
            spacing = 20.px
            paddingLeft = 20.0
            paddingRight = 20.0
        }

        val form = form {
            fieldset {
                field(messages["source_folder"]) {
                    jfxTextfield(sourceDirProperty) {
                        setValidators(RequiredFieldValidator())
                        isDisable = true
                    }
                    spacer { }
                    jfxButton(messages["choose_folder"].toUpperCase()) {
                        action {
                            val chooser = DirectoryChooser()
                            chooser.title = messages["source_folder"]
                            if (sourceDir != null) chooser.initialDirectory = File(sourceDir)
                            sourceDir = chooser.showDialog(currentWindow)?.absolutePath ?: sourceDir
                        }
                    }
                }

                field(messages["ftp_server"]) {
                    jfxTextfield(ftpServerProperty) {
                        setValidators(RequiredFieldValidator())
                    }
                }

                field(messages["ftp_user"]) {
                    jfxTextfield(ftpUserProperty) {
                        setValidators(RequiredFieldValidator())
                    }
                }

                field(messages["ftp_password"]) {
                    jfxPasswordfield(ftpPasswordProperty) {
                        setValidators(RequiredFieldValidator())
                    }
                }

                field(messages["ftp_port"]) {
                    jfxTextfield {
                        setValidators(NumberValidator())
                        textProperty().bindBidirectional(ftpPortProperty,
                                NumberStringConverter("##0"))
                    }
                }
            }
        }

        jfxButton(messages["save"].toUpperCase()) {
            action {
                var valid = (form.children[0] as Fieldset).children.map {
                    if (it is Field) {
                        val field = it.inputs[0]
                        if (field is JFXTextField) {
                            return@map field.validate()
                        }
                    }
                    return@map true
                }.all { it }

                if (valid) {
                    configStore.sourceDir = sourceDir
                    configStore.ftpServer = ftpServer
                    configStore.ftpUser = ftpUser
                    configStore.ftpPassword = ftpPassword
                    configStore.ftpPort = ftpPort
                    SyncDaemon(app).start()
                }
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