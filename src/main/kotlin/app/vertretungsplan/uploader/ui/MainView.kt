package app.vertretungsplan.uploader.ui

import app.vertretungsplan.uploader.BuildInfo
import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import app.vertretungsplan.uploader.sync.FileInfo
import app.vertretungsplan.uploader.sync.Sync.Callback
import app.vertretungsplan.uploader.ui.helpers.*
import app.vertretungsplan.uploader.ui.style.MainStyleSheet
import app.vertretungsplan.uploader.ui.style.STYLE_BACKGROUND_COLOR
import com.jfoenix.controls.*
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import com.jfoenix.validation.NumberValidator
import com.jfoenix.validation.RequiredFieldValidator
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.TreeTableView
import javafx.scene.image.Image
import javafx.scene.layout.StackPane
import javafx.util.converter.NumberStringConverter
import org.apache.commons.vfs2.VFS
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

    val ftpDirProperty = SimpleStringProperty(configStore.ftpDir)
    var ftpDir by ftpDirProperty

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

        protocolProperty.addListener { _, _, new ->
            ftpPort = when (new) {
                "FTP", "FTPS" -> 21
                "SFTP" -> 22
                else -> 0
            }
        }

        if (isWindows()) {
            autostart = Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER,
                    "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "vertretungsplan-uploader")

            autostartProperty.addListener {_, _, new ->
                val installationDir = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                        "Software\\vertretungsplan-uploader", "")

                if (installationDir != null) {
                    if (new) {
                        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                                "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "vertretungsplan-uploader",
                                "\"$installationDir\\uploader.exe\"")
                    }
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
                            val initDir = if (sourceDir != null) File(sourceDir) else null
                            sourceDir = chooseDirectory(messages["source_folder"], initDir)?.absolutePath ?: sourceDir
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

                field(messages["ftp_folder"]) {
                    jfxTextfield(ftpDirProperty) {
                        setValidators(RequiredFieldValidator())
                        isDisable = true
                    }
                    spacer { }
                    jfxButton(messages["choose_folder"].toUpperCase()) {
                        action {
                            val man = VFS.getManager()
                            val url = "${protocol.toLowerCase()}://$ftpUser:$ftpPassword@$ftpServer:$ftpPort/"
                            val dest = man.resolveFile(url)

                            val dialog = JFXDialog()
                            dialog.content = vbox {
                                val treetable = TreeTableView<FileInfo>().apply {
                                    column("Name", FileInfo::getName)
                                    root = RecursiveTreeItem<FileInfo>(FileInfo(dest, true),
                                            RecursiveTreeObject<FileInfo>::getChildren)
                                    root.isExpanded = true
                                    resizeColumnsToFitContent()
                                }
                                this.add(treetable)
                                hbox {
                                    jfxButton(messages["ok"]) {
                                        action {
                                            if (treetable.selectedItem != null) {
                                                ftpDir = treetable.selectedItem!!.file.name.path
                                                dialog.close()
                                            }
                                        }
                                    }
                                    jfxButton(messages["cancel"]) {
                                        action {
                                            dialog.close()
                                        }
                                    }
                                }
                            }
                            dialog.show(root)
                        }
                    }
                }
            }

            fieldset(messages["group_app_settings"]) {
                field(messages["autostart"]) {
                    jfxCheckbox(property=autostartProperty)
                    this.isVisible = isWindows()
                }
            }
        }

        jfxButton(messages["save"].toUpperCase()) {
            addClass(MainStyleSheet.raisedButton)
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
                    configStore.ftpDir = ftpDir
                    (app as VertretungsplanUploaderMain).restartDaemon()
                }
            }
        }

        hbox {
            label(messages["version"].format(BuildInfo().version))
            spacer {}
            jfxButton(messages["licenses"].toUpperCase()) {
                action {
                    val closeButton: JFXButton = this.jfxButton(messages.getString("dialog_close"))
                    val dialog = this.jfxDialog(transitionType = JFXDialog.DialogTransition.BOTTOM) {
                        setHeading(label(messages.getString("settings_licenses")))
                        val text = resources.stream("licenses.txt").bufferedReader().use { it.readText() }
                        setBody(jfxTextarea(text) {
                                    style {
                                        minHeight = 300.px
                                        maxHeight = 300.px
                                    }

                                    isEditable = false
                                })
                        setActions(closeButton)
                    }
                    closeButton.action {
                        dialog.close()
                    }
                    dialog.show(root)
                }
            }
        }

    }

    private fun isWindows() = com.sun.jna.Platform.getOSType() == com.sun.jna.Platform.WINDOWS

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