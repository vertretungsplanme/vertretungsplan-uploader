package app.vertretungsplan.uploader

import java.util.prefs.Preferences

class VertretungsplanUploaderPrefs(private var data_dir: String) {
    private val prefs = Preferences.userNodeForPackage(VertretungsplanUploaderPrefs::class.java)

    private val PREFS_KEY_SOURCE_DIR = "source_dir"

    var sourceDir: String?
        get() = prefs.get(PREFS_KEY_SOURCE_DIR, null)
        set (value) {
            prefs.put(PREFS_KEY_SOURCE_DIR, value)
            prefs.flush()
        }
}