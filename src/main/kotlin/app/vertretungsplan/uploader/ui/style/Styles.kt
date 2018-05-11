package app.vertretungsplan.uploader.ui.style

import javafx.geometry.Pos
import tornadofx.*

val STYLE_BACKGROUND_COLOR = "#f6f1f9"
val STYLE_PRIMARY_COLOR = "#5677fc"
val STYLE_PRIMARY_DARK_COLOR = "#455ede"
val STYLE_TOOLBAR_TEXT_COLOR = "#ffffff"
val STYLE_ACCENT_COLOR = "#ffc400"

class MainStyleSheet : Stylesheet() {
    companion object {
        val toolBar by cssclass()
    }

    init {
        toolBar {
            alignment = Pos.CENTER
            backgroundColor += c(STYLE_PRIMARY_COLOR)
            minHeight = 48.px
            maxHeight = 48.px

            select("JFXToggleButton") {
                select("LabeledText") {
                    fill = c(STYLE_TOOLBAR_TEXT_COLOR)
                    fontFamily = "Roboto"
                }
            }
            select("JFXButton") {
                select("LabeledText") {
                    fill = c(STYLE_TOOLBAR_TEXT_COLOR)
                }
            }
            label {
                textFill = c(STYLE_TOOLBAR_TEXT_COLOR)
            }
        }
    }

}