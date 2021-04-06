package com.chartiq.demo

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.chartiq.demo.ui.settings.language.ChartIQLanguage
import com.chartiq.sdk.model.drawingtool.DrawingTool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

interface ApplicationPrefs {

    val languageState: Flow<ChartIQLanguage>

    fun saveDrawingTool(tool: DrawingTool)

    fun getDrawingTool(): DrawingTool

    fun saveFavoriteDrawingTools(drawingToolsSet: Set<DrawingTool>)

    fun getFavoriteDrawingTools(): Set<DrawingTool>

    fun getApplicationId(): String

    fun setLanguage(language: ChartIQLanguage)

    class Default(context: Context) : ApplicationPrefs {

        private val prefs: SharedPreferences by lazy {
            context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        }
        private val language = MutableStateFlow(ChartIQLanguage.EN)

        override val languageState = language.asStateFlow()

        init {
            language.value =
                ChartIQLanguage.valueOf(prefs.getString(KEY_LANGUAGE, ChartIQLanguage.EN.name)!!)
        }

        override fun saveDrawingTool(tool: DrawingTool) = prefs.edit(true) {
            putString(KEY_DRAWING_TOOL, tool.toString())
        }

        override fun getDrawingTool(): DrawingTool =
            DrawingTool.valueOf(prefs.getString(KEY_DRAWING_TOOL, DrawingTool.NONE.toString())!!)

        override fun saveFavoriteDrawingTools(drawingToolsSet: Set<DrawingTool>) =
            prefs.edit(true) {
                val set = drawingToolsSet
                    .map { it.toString() }
                    .toHashSet()
                putStringSet(KEY_DRAWING_TOOL_FAVORITE, set)
            }

        override fun getFavoriteDrawingTools(): Set<DrawingTool> {
            return prefs.getStringSet(KEY_DRAWING_TOOL_FAVORITE, setOf())!!
                .map { DrawingTool.valueOf(it.toUpperCase()) }
                .toHashSet()
        }

        override fun getApplicationId(): String {
            val storedId = prefs.getString(KEY_APPLICATION_ID, null)
            return if (storedId == null) {
                val newId = UUID.randomUUID().toString()
                prefs.edit(true) {
                    putString(KEY_APPLICATION_ID, newId)
                }
                newId
            } else {
                storedId
            }
        }

        override fun setLanguage(lang: ChartIQLanguage) {
            prefs.edit(true) {
                putString(KEY_LANGUAGE, lang.name)
            }
            language.value = lang
        }
    }

    companion object {
        private const val PREF_FILE = "ApplicationPrefs"

        private const val KEY_DRAWING_TOOL_FAVORITE = "chart.iq.demo.chart.drawingtool.favorites"
        private const val KEY_DRAWING_TOOL = "chart.iq.demo.chart.drawingtool.tool"
        private const val KEY_LANGUAGE = "chart.iq.demo.settings.chartiq_language"
        private const val KEY_APPLICATION_ID = "chart.iq.demo.chart.applicationid"
    }
}
