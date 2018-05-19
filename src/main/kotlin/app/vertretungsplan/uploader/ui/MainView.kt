package app.vertretungsplan.uploader.ui

import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import app.vertretungsplan.uploader.ui.style.MainStyleSheet
import app.vertretungsplan.uploader.ui.style.STYLE_BACKGROUND_COLOR
import app.vertretungsplan.uploader.sync.Sync.Callback
import app.vertretungsplan.uploader.ui.helpers.*
import com.jfoenix.controls.JFXSpinner
import com.jfoenix.controls.JFXTextField
import com.jfoenix.validation.NumberValidator
import com.jfoenix.validation.RequiredFieldValidator
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.image.Image
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import javafx.stage.DirectoryChooser
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import java.io.File

class MainView : View() {
    private var configStore = (app as VertretungsplanUploaderMain).configStore

    val sourceDirProperty = SimpleStringProperty(configStore.sourceDir)
    var sourceDir by sourceDirProperty

    val protocolProperty = SimpleStringProperty(configStore.protocol)
    val protocol by protocolProperty

    val ftpServerProperty = SimpleStringProperty(configStore.ftpServer)
    var ftpServer by ftpServerProperty

    val ftpUserProperty = SimpleStringProperty(configStore.ftpUser)
    var ftpUser by ftpUserProperty

    val ftpPasswordProperty = SimpleStringProperty(configStore.ftpPassword)
    var ftpPassword by ftpPasswordProperty

    val ftpPortProperty = SimpleIntegerProperty(configStore.ftpPort)
    var ftpPort by ftpPortProperty

    val statusProperty = SimpleStringProperty("")
    var status by statusProperty

    val syncingProperty = SimpleBooleanProperty(false)
    var syncing by syncingProperty

    val autostartProperty = SimpleBooleanProperty(false)
    var autostart by autostartProperty

    init {
        (app as VertretungsplanUploaderMain).daemon!!.callback = object : Callback {
            override fun start() {
                Platform.runLater {
                    syncing = true
                }
            }

            override fun newFile(file: String) {
                Platform.runLater { status = messages["syncing"].format(file) }
            }

            override fun end() {
                Platform.runLater {
                    syncing = false
                    status = ""
                }
            }
        }
    }

    private val contentBox = vbox {
        useMaxHeight = true

        style {
            alignment = Pos.CENTER
            backgroundColor += c(STYLE_BACKGROUND_COLOR)
            spacing = 20.px
            paddingLeft = 20.0
            paddingRight = 20.0
            paddingBottom = 20.0
        }

        val form = form {
            fieldset(messages["group_source"]) {
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
            }

            fieldset(messages["group_ftp"]) {
                field(messages["ftp_protocol"]) {
                    jfxComboBox(protocolProperty, observableList("FTP", "FTPS", "SFTP"))
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

            fieldset(messages["group_app_settings"]) {
                field(messages["autostart"]) {
                    jfxCheckbox()
                }
            }
        }

        jfxButton(messages["save"].toUpperCase()) {
            action {
                val valid = (form.children[0] as Fieldset).children.map {
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
                    configStore.protocol = protocol
                    configStore.ftpServer = ftpServer
                    configStore.ftpUser = ftpUser
                    configStore.ftpPassword = ftpPassword
                    configStore.ftpPort = ftpPort
                    (app as VertretungsplanUploaderMain).restartDaemon()
                }
            }
        }

    }

    override val root: StackPane = stackpane {
        vbox {
            useMaxHeight = true

            hbox {
                addClass(MainStyleSheet.toolBar)

                useMaxWidth = true

                imageview(Image(resources.stream("vertretungsplan_logo.svg"), 198.0, 32.0, true,
                        false))
                spacer { }
                label(statusProperty) {
                    style {
                        minWidth = 200.px
                        alignment = Pos.CENTER_RIGHT
                        paddingRight = 16.0
                    }
                }
                jfxSpinner(SimpleDoubleProperty(JFXSpinner.INDETERMINATE_PROGRESS)) {
                    visibleWhen(syncingProperty)
                    radius = 10.0
                }
            }

            spacer { }
            this += contentBox
            spacer { }

        }
    }

    override fun onUndock() {
        (app as VertretungsplanUploaderMain).daemon!!.callback = null
    }
}