package app.vertretungsplan.uploader.ui

import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import app.vertretungsplan.uploader.ui.style.MainStyleSheet
import app.vertretungsplan.uploader.ui.style.STYLE_BACKGROUND_COLOR
import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import app.vertretungsplan.uploader.ui.style.STYLE_PRIMARY_DARK_COLOR
import javafx.scene.image.Image
import tornadofx.*

class MainView : View() {
    private val contentBox = vbox {
        useMaxHeight = true

        style {
            alignment = Pos.CENTER
            backgroundColor += c(STYLE_BACKGROUND_COLOR)
            spacing = 20.px
        }

        hbox {
            style {
                paddingBottom = 20.0
                alignment = Pos.CENTER
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
                        imageview(Image(VertretungsplanUploaderMain::class.java.getResourceAsStream("vertretungsplan_logo.svg"), 0.0, 32.0, true, false))
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