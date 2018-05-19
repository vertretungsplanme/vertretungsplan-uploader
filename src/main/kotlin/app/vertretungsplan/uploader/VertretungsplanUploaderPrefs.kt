package app.vertretungsplan.uploader

import java.util.prefs.Preferences

class VertretungsplanUploaderPrefs(private var data_dir: String) {
    private val prefs = Preferences.userNodeForPackage(VertretungsplanUploaderPrefs::class.java)

    private val PREFS_KEY_SOURCE_DIR = "source_dir"
    private val PREFS_KEY_PROTOCOL = "protocol"
    private val PREFS_KEY_FTP_SERVER = "ftp_server"
    private val PREFS_KEY_FTP_USER = "ftp_user"
    private val PREFS_KEY_FTP_PASSWORD = "ftp_password"
    private val PREFS_KEY_FTP_PORT = "ftp_port"

    var sourceDir: String?
        get() = prefs.get(PREFS_KEY_SOURCE_DIR, null)
        set (value) {
            prefs.put(PREFS_KEY_SOURCE_DIR, value)
            prefs.flush()
        }

    var ftpServer: String?
        get() = prefs.get(PREFS_KEY_FTP_SERVER, null)
        set (value) {
            prefs.put(PREFS_KEY_FTP_SERVER, value)
            prefs.flush()
        }

    var protocol: String
        get() = prefs.get(PREFS_KEY_PROTOCOL, "FTP")
        set (value) {
            prefs.put(PREFS_KEY_PROTOCOL, value)
            prefs.flush()
        }

    var ftpUser: String?
        get() = prefs.get(PREFS_KEY_FTP_USER, null)
        set (value) {
            prefs.put(PREFS_KEY_FTP_USER, value)
            prefs.flush()
        }

    var ftpPassword: String?
        get() = prefs.get(PREFS_KEY_FTP_PASSWORD, null)
        set (value) {
            prefs.put(PREFS_KEY_FTP_PASSWORD, value)
            prefs.flush()
        }

    var ftpPort: Int
        get() = prefs.getInt(PREFS_KEY_FTP_PORT, 22)
        set (value) {
            prefs.putInt(PREFS_KEY_FTP_PORT, value)
            prefs.flush()
        }
}