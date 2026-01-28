package com.webcoderpro

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var editor: EditText
    private lateinit var webView: WebView
    private lateinit var fileList: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var projectDir: File
    private var currentFile: File? = null
    private val fileNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editor = findViewById(R.id.editor)
        webView = findViewById(R.id.webView)
        fileList = findViewById(R.id.fileList)

        val btnEditor = findViewById<Button>(R.id.btnEditor)
        val btnPreview = findViewById<Button>(R.id.btnPreview)
        val btnNew = findViewById<Button>(R.id.btnNew)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        projectDir = File(filesDir, "project")
        if (!projectDir.exists()) projectDir.mkdir()

        loadFiles()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames)
        fileList.adapter = adapter

        if (fileNames.isNotEmpty()) {
            openFile(File(projectDir, fileNames[0]))
        }

        btnEditor.setOnClickListener {
            editor.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }

        btnPreview.setOnClickListener {
            saveCurrent()
            editor.visibility = View.GONE
            webView.visibility = View.VISIBLE
            webView.loadDataWithBaseURL(
                null,
                editor.text.toString(),
                "text/html",
                "UTF-8",
                null
            )
        }

        btnNew.setOnClickListener {
            newFileDialog()
        }

        fileList.setOnItemClickListener { _, _, pos, _ ->
            openFile(File(projectDir, fileNames[pos]))
        }
    }

    private fun loadFiles() {
        fileNames.clear()
        projectDir.listFiles()?.forEach {
            fileNames.add(it.name)
        }

        if (fileNames.isEmpty()) {
            val f = File(projectDir, "index.html")
            f.writeText(defaultHtml())
            fileNames.add(f.name)
        }
    }

    private fun openFile(file: File) {
        currentFile = file
        editor.setText(file.readText())
    }

    private fun saveCurrent() {
        currentFile?.writeText(editor.text.toString())
    }

    private fun newFileDialog() {
        val input = EditText(this)
        input.hint = "page.html"

        AlertDialog.Builder(this)
            .setTitle("New HTML File")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    val f = File(projectDir, name)
                    f.writeText(defaultHtml())
                    fileNames.add(f.name)
                    adapter.notifyDataSetChanged()
                    openFile(f)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun defaultHtml(): String {
        return """
            <html>
            <body>
                <h1>WebCoderPro 🚀</h1>
                <p>Multiple file project</p>
            </body>
            </html>
        """.trimIndent()
    }
}
