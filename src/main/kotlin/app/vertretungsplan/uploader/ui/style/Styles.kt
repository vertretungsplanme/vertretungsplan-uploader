package app.vertretungsplan.uploader.ui.style

import com.jfoenix.controls.JFXButton
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

val STYLE_BACKGROUND_COLOR = "#f6f1f9"
val STYLE_PRIMARY_COLOR = "#5677fc"
val STYLE_PRIMARY_DARK_COLOR = "#455ede"
val STYLE_TOOLBAR_TEXT_COLOR = "#ffffff"
val STYLE_ACCENT_COLOR = "#ffc400"

class MainStyleSheet : Stylesheet() {
    companion object {
        val toolBar by cssclass()
        val settingLabel by cssclass()
        val raisedButton by cssclass()

        val buttonType by cssproperty<JFXButton.ButtonType>("-jfx-button-type")
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

            select("JFXSpinner .arc") {
                stroke = c("#FFFFFF")
                strokeWidth = 4.px
            }
        }

        settingLabel {
            fontFamily = "Roboto"
            fontWeight = FontWeight.BOLD
            padding = box(0.px, 20.px, 0.px, 0.px)
        }

        raisedButton {
            buttonType.value = JFXButton.ButtonType.RAISED
            backgroundColor += c(STYLE_PRIMARY_COLOR)
            textFill = Color.WHITE
        }
    }

}