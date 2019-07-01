package app.vertretungsplan.uploader

import app.vertretungsplan.uploader.BuildInfo
import java.net.URLEncoder
import java.util.prefs.Preferences

class VertretungsplanUploaderPrefs(private var data_dir: String) {
    private val prefs = Preferences.userNodeForPackage(VertretungsplanUploaderPrefs::class.java)

    private val PREFS_KEY_SOURCE_DIR = "source_dir"
    private val PREFS_KEY_PROTOCOL = "protocol"
    private val PREFS_KEY_FTP_SERVER = "ftp_server"
    private val PREFS_KEY_FTP_USER = "ftp_user"
    private val PREFS_KEY_FTP_PASSWORD = "ftp_password"
    private val PREFS_KEY_FTP_PORT = "ftp_port"
    private val PREFS_KEY_FTP_DIR = "ftp_dir"
    private val PREFS_KEY_LAST_UPDATE_CHECK = "last_update_check_"
    private val PREFS_KEY_UPDATE_CHECK_NEWER_VERSION = "update_check_newer_version_"

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
        get() = prefs.getInt(PREFS_KEY_FTP_PORT, 21)
        set (value) {
            prefs.putInt(PREFS_KEY_FTP_PORT, value)
            prefs.flush()
        }

    var ftpDir: String
        get() = prefs.get(PREFS_KEY_FTP_DIR, "")
        set (value) {
            prefs.put(PREFS_KEY_FTP_DIR, value)
            prefs.flush()
        }

    val destUrl: String?
        get() = if (ftpUser != null && ftpPassword != null && ftpServer != null)
            "${protocol.toLowerCase()}://${urlencode(ftpUser!!)}:${urlencode(ftpPassword!!)}@$ftpServer:$ftpPort/$ftpDir" else null

    private fun urlencode(s: String): String {
        return URLEncoder.encode(s, "utf-8")
    }

    var lastUpdateCheck: Long
        get() = prefs.getLong(PREFS_KEY_LAST_UPDATE_CHECK + BuildInfo().version, 0)
        set(value) {
            prefs.putLong(PREFS_KEY_LAST_UPDATE_CHECK + BuildInfo().version, value)
            prefs.flush()
        }

    var updateCheckNewerVersion: String
        get() = prefs.get(PREFS_KEY_UPDATE_CHECK_NEWER_VERSION + BuildInfo().version, "")
        set (value) {
            prefs.put(PREFS_KEY_UPDATE_CHECK_NEWER_VERSION + BuildInfo().version, value)
            prefs.flush()
        }
}