package app.vertretungsplan.uploader.ui

import app.vertretungsplan.uploader.VertretungsplanUploaderMain
import app.vertretungsplan.uploader.BuildInfo
import com.github.kittinunf.fuel.httpPost
import org.json.JSONObject
import tornadofx.Controller

class MainController: Controller() {
    protected var configStore = (app as VertretungsplanUploaderMain).configStore

    fun updateCheck() {
        if ((System.currentTimeMillis() - configStore.lastUpdateCheck) > 24 * 3600 * 1000) {
            val payload = JSONObject()
            payload.put("version", BuildInfo().version)
            val request = "https://api.vertretungsplan.me/v2/.update_check/uploader/".httpPost().body(payload
                    .toString())
            request.headers["Content-Type"] = "application/json"
            val (_, response, result) = request.responseString()
            if (response.statusCode == 200) {
                try {
                    val data = JSONObject(result.get())
                    if ("ok".equals(data.optString("status"))) {
                        if (data.getJSONObject("version").getBoolean("updatable")) {
                            configStore.lastUpdateCheck = System.currentTimeMillis()
                            configStore.updateCheckNewerVersion = data.getJSONObject("version").getString("latest")
                        } else {
                            configStore.lastUpdateCheck = System.currentTimeMillis()
                            configStore.updateCheckNewerVersion = ""
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
