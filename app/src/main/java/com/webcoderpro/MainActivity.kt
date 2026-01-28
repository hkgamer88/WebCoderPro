package com.webcoderpro

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var htmlEditor: EditText
    private lateinit var webView: WebView
    private lateinit var filesDirPath: File
    private lateinit var fileAdapter: ArrayAdapter<String>

    private val fileNames = mutableListOf<String>()
    private var currentFile: File? = null
    private var isDark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Views
        htmlEditor = findViewById(R.id.htmlEditor)
        webView = findViewById(R.id.webView)
        val editorTab = findViewById<Button>(R.id.tabEditor)
        val previewTab = findViewById<Button>(R.id.tabPreview)
        val darkBtn = findViewById<Button>(R.id.btnDark)
        val exportBtn = findViewById<Button>(R.id.btnExport)
        val newFileBtn = findViewById<Button>(R.id.btnNewFile)
        val fileList = findViewById<ListView>(R.id.fileList)

        // WebView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Files
        filesDirPath = File(filesDir, "projects")
        if (!filesDirPath.exists()) filesDirPath.mkdir()

        loadFiles()
        fileAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames)
        fileList.adapter = fileAdapter

        htmlEditor.setText("""
            <html>
            <body>
                <h1>WebCoderPro 🚀</h1>
                <button onclick="alert('JS Works')">Test JS</button>
            </body>
            </html>
        """.trimIndent())

        editorTab.setOnClickListener {
            htmlEditor.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }

        previewTab.setOnClickListener {
            currentFile?.writeText(htmlEditor.text.toString())
            htmlEditor.visibility = View.GONE
            webView.visibility = View.VISIBLE
            webView.loadDataWithBaseURL(
                null,
                htmlEditor.text.toString(),
                "text/html",
                "UTF-8",
                null
            )
        }

        darkBtn.setOnClickListener {
            isDark = !isDark
            applyTheme()
        }

        exportBtn.setOnClickListener {
            exportProjectAsZip()
        }

        fileList.setOnItemClickListener { _, _, position, _ ->
            val file = File(filesDirPath, fileNames[position])
            currentFile = file
            htmlEditor.setText(file.readText())
        }

        fileList.setOnItemLongClickListener { _, _, position, _ ->
            showFileOptions(File(filesDirPath, fileNames[position]))
            true
        }

        newFileBtn.setOnClickListener {
            createNewFileDialog()
        }
    }

    private fun loadFiles() {
        fileNames.clear()
        filesDirPath.listFiles()?.forEach { fileNames.add(it.name) }
    }

    private fun createNewFileDialog() {
        val input = EditText(this)
        input.hint = "index.html"

        AlertDialog.Builder(this)
            .setTitle("New HTML File")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val file = File(filesDirPath, input.text.toString())
                file.writeText("<html><body><h1>${file.name}</h1></body></html>")
                currentFile = file
                loadFiles()
                fileAdapter.notifyDataSetChanged()
                htmlEditor.setText(file.readText())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showFileOptions(file: File) {
        AlertDialog.Builder(this)
            .setTitle(file.name)
            .setItems(arrayOf("Rename", "Delete")) { _, which ->
                if (which == 0) renameFileDialog(file) else deleteFile(file)
            }
            .show()
    }

    private fun renameFileDialog(file: File) {
        val input = EditText(this)
        input.setText(file.name)

        AlertDialog.Builder(this)
            .setTitle("Rename File")
            .setView(input)
            .setPositiveButton("Rename") { _, _ ->
                val newFile = File(filesDirPath, input.text.toString())
                file.renameTo(newFile)
                loadFiles()
                fileAdapter.notifyDataSetChanged()
                currentFile = newFile
                htmlEditor.setText(newFile.readText())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteFile(file: File) {
        file.delete()
        loadFiles()
        fileAdapter.notifyDataSetChanged()
        if (currentFile == file) htmlEditor.setText("")
    }

    private fun exportProjectAsZip() {
        val zipFile = File(filesDir, "WebCoderPro_Project.zip")
        val zipOut = ZipOutputStream(FileOutputStream(zipFile))

        filesDirPath.listFiles()?.forEach {
            zipOut.putNextEntry(ZipEntry(it.name))
            FileInputStream(it).copyTo(zipOut)
            zipOut.closeEntry()
        }

        zipOut.close()
        Toast.makeText(this, "ZIP Created", Toast.LENGTH_SHORT).show()
    }

    private fun applyTheme() {
        if (isDark) {
            htmlEditor.setBackgroundColor(Color.parseColor("#121212"))
            htmlEditor.setTextColor(Color.WHITE)
        } else {
            htmlEditor.setBackgroundColor(Color.WHITE)
            htmlEditor.setTextColor(Color.BLACK)
        }
    }
}
