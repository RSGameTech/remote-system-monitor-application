package `in`.rsgametech.systemmonitor.data

import android.content.Context
import `in`.rsgametech.systemmonitor.model.IconType
import `in`.rsgametech.systemmonitor.model.MonitorItem
import org.json.JSONArray
import org.json.JSONObject

class MonitorItemStorage(context: Context) {

    private val prefs = context.getSharedPreferences("monitor_items", Context.MODE_PRIVATE)

    fun load(): List<MonitorItem> {
        val json = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val obj = array.getJSONObject(i)
            MonitorItem(
                id = obj.getInt("id"),
                iconType = IconType.valueOf(obj.getString("iconType")),
                label = obj.getString("label"),
                supportingText = obj.getString("supportingText"),
                apiKey = obj.optString("apiKey", "")
            )
        }
    }

    fun save(items: List<MonitorItem>) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("iconType", item.iconType.name)
                put("label", item.label)
                put("supportingText", item.supportingText)
                put("apiKey", item.apiKey)
            })
        }
        prefs.edit().putString(KEY_ITEMS, array.toString()).apply()
    }

    companion object {
        private const val KEY_ITEMS = "items"
    }
}
