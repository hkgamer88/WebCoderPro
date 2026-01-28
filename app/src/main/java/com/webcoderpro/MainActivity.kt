package com.webcoderpro

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var editor: EditText
    private lateinit var webView: WebView
    private lateinit var fileList: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private lateinit var projectDir: File
    private var currentFile: File? = null
    private val fileNames = mutableListOf<String>()

    private var isDark = false
    private var isHighlighting = false

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

        // Syntax highlighting watcher
        editor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isHighlighting || s == null) return
                isHighlighting = true
                applySyntaxHighlight(s)
                isHighlighting = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
                wrapWithTheme(editor.text.toString()),
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

        fileList.setOnItemLongClickListener { _, _, pos, _ ->
            showFileOptions(File(projectDir, fileNames[pos]))
            true
        }

        editor.setOnLongClickListener {
            isDark = !isDark
            applyEditorTheme()
            Toast.makeText(this, if (isDark) "Dark Mode" else "Light Mode", Toast.LENGTH_SHORT).show()
            true
        }

        applyEditorTheme()
    }

    // ================= SYNTAX HIGHLIGHT =================

    private fun applySyntaxHighlight(editable: Editable) {
        editable.getSpans(0, editable.length, ForegroundColorSpan::class.java)
            .forEach { editable.removeSpan(it) }

        val text = editable.toString()

        highlight("<[^>]+>", text, editable, Color.parseColor("#4CAF50"))       // HTML tags
        highlight("\\b(id|class|src|href|style)\\b", text, editable, Color.parseColor("#03A9F4"))
        highlight("\"[^\"]*\"", text, editable, Color.parseColor("#FFC107"))    // Strings
        highlight("<!--(.|\\n)*?-->", text, editable, Color.GRAY)               // Comments
        highlight("\\b(function|var|let|const|if|else|return)\\b",
            text, editable, Color.parseColor("#E91E63"))                        // JS keywords
    }

    private fun highlight(regex: String, text: String, editable: Editable, color: Int) {
        val matcher = Pattern.compile(regex).matcher(text)
        while (matcher.find()) {
            editable.setSpan(
                ForegroundColorSpan(color),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    // ================= ZIP EXPORT =================

    private fun exportProjectAsZip() {
        val zipFile = File(filesDir, "WebCoderPro_Project.zip")
        val zipOut = ZipOutputStream(FileOutputStream(zipFile))

        projectDir.listFiles()?.forEach {
            zipOut.putNextEntry(ZipEntry(it.name))
            FileInputStream(it).copyTo(zipOut)
            zipOut.closeEntry()
        }

        zipOut.close()
        Toast.makeText(this, "ZIP created", Toast.LENGTH_SHORT).show()
    }

    // ================= FILE SYSTEM =================

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
                val f = File(projectDir, input.text.toString())
                f.writeText(defaultHtml())
                fileNames.add(f.name)
                adapter.notifyDataSetChanged()
                openFile(f)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showFileOptions(file: File) {
        AlertDialog.Builder(this)
            .setTitle(file.name)
            .setItems(arrayOf("Rename", "Delete", "Export ZIP")) { _, which ->
                when (which) {
                    0 -> renameFileDialog(file)
                    1 -> deleteFile(file)
                    2 -> exportProjectAsZip()
                }
            }
            .show()
    }

    private fun renameFileDialog(file: File) {
        val input = EditText(this)
        input.setText(file.name)

        AlertDialog.Builder(this)
            .setTitle("Rename")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val newFile = File(projectDir, input.text.toString())
                file.renameTo(newFile)
                loadFiles()
                adapter.notifyDataSetChanged()
                openFile(newFile)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteFile(file: File) {
        file.delete()
        loadFiles()
        adapter.notifyDataSetChanged()
        if (fileNames.isNotEmpty()) {
            openFile(File(projectDir, fileNames[0]))
        } else {
            editor.setText("")
        }
    }

    // ================= THEME =================

    private fun applyEditorTheme() {
        if (isDark) {
            editor.setBackgroundColor(Color.parseColor("#121212"))
            editor.setTextColor(Color.WHITE)
            editor.setHintTextColor(Color.LTGRAY)
        } else {
            editor.setBackgroundColor(Color.WHITE)
            editor.setTextColor(Color.BLACK)
            editor.setHintTextColor(Color.DKGRAY)
        }
    }

    private fun wrapWithTheme(html: String): String {
        val bg = if (isDark) "#121212" else "#FFFFFF"
        val fg = if (isDark) "#FFFFFF" else "#000000"
        return """
            <html>
            <head>
                <style>
                    body { background:$bg; color:$fg; padding:16px; }
                </style>
            </head>
            <body>
                $html
            </body>
            </html>
        """.trimIndent()
    }

    private fun defaultHtml(): String {
        return """
            <h1>WebCoderPro 🚀</h1>
            <p>Syntax highlighting enabled</p>
        """.trimIndent()
    }
}
