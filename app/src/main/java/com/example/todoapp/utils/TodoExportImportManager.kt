package com.example.todoapp.utils

import android.content.Context
import android.net.Uri
import com.example.todoapp.data.TodoEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

object TodoExportImportManager {
    private val gson = Gson()

    data class ExportData(
        val version: Int = 1,
        val exportedAt: Long = System.currentTimeMillis(),
        val todos: List<TodoEntity>
    )

    /**
     * Export todos to a JSON file
     * @param context Application context
     * @param uri The URI where to save the file
     * @param todos List of todos to export
     * @return True if export was successful, false otherwise
     */
    fun exportTodos(context: Context, uri: Uri, todos: List<TodoEntity>): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    val exportData = ExportData(todos = todos)
                    val json = gson.toJson(exportData)
                    writer.write(json)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Import todos from a JSON file
     * @param context Application context
     * @param uri The URI of the file to import
     * @return List of imported todos, or null if import failed
     */
    fun importTodos(context: Context, uri: Uri): List<TodoEntity>? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val json = reader.readText()
                    val type = object : TypeToken<ExportData>() {}.type
                    val exportData: ExportData = gson.fromJson(json, type)

                    // Reset IDs and timestamps for imported todos
                    exportData.todos.map { todo ->
                        todo.copy(
                            id = 0, // Will be auto-generated
                            isCompleted = false, // Reset completion status
                            createdAt = System.currentTimeMillis(),
                            lastModifiedAt = System.currentTimeMillis()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate a default filename for export
     */
    fun generateExportFileName(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        return "todos_export_$timestamp.json"
    }
}
