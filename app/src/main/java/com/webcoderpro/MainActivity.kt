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
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var htmlEditor: EditText
    private lateinit var webView: WebView
    private lateinit var projectSpinner: Spinner
    private lateinit var fileList: ListView
    private lateinit var fileAdapter: ArrayAdapter<String>
    private lateinit var projectsRoot: File
    private lateinit var currentProject: File

    private val fileNames = mutableListOf<String>()
    private var currentFile: File? = null
    private var isDark = false
    private var isHighlighting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        htmlEditor = findViewById(R.id.htmlEditor)
        webView = findViewById(R.id.webView)
        projectSpinner = findViewById(R.id.projectSpinner)
        fileList = findViewById(R.id.fileList)

        val editorTab = findViewById<ImageButton>(R.id.tabEditor)
        val previewTab = findViewById<ImageButton>(R.id.tabPreview)
        val darkBtn = findViewById<ImageButton>(R.id.btnDark)
        val exportBtn = findViewById<ImageButton>(R.id.btnExport)
        val newFileBtn = findViewById<Button>(R.id.btnNewFile)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        projectsRoot = File(filesDir, "projects")
        if (!projectsRoot.exists()) projectsRoot.mkdir()

        setupProjects()

        htmlEditor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isHighlighting || s == null) return
                isHighlighting = true
                applySyntaxHighlight(s)
                isHighlighting = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editorTab.setOnClickListener {
            htmlEditor.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }

        previewTab.setOnClickListener {
            currentFile?.writeText(htmlEditor.text.toString())
            htmlEditor.visibility = View.GONE
            webView.visibility = View.VISIBLE
            webView.loadDataWithBaseURL(null, htmlEditor.text.toString(), "text/html", "UTF-8", null)
        }

        darkBtn.setOnClickListener {
            isDark = !isDark
            applyTheme()
        }

        exportBtn.setOnClickListener {
            exportProjectAsZip()
        }

        newFileBtn.setOnClickListener {
            createNewFileDialog()
        }

        fileList.setOnItemClickListener { _, _, position, _ ->
            val file = File(currentProject, fileNames[position])
            currentFile = file
            htmlEditor.setText(file.readText())
        }
    }

    private fun setupProjects() {
        if (projectsRoot.listFiles().isNullOrEmpty()) {
            File(projectsRoot, "MyProject").mkdir()
        }
        val projects = projectsRoot.listFiles()?.map { it.name } ?: emptyList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, projects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        projectSpinner.adapter = adapter

        projectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                currentProject = File(projectsRoot, projects[pos])
                loadFiles()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadFiles() {
        fileNames.clear()
        currentProject.listFiles()?.forEach { fileNames.add(it.name) }
        fileAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames)
        fileList.adapter = fileAdapter
    }

    private fun createNewFileDialog() {
        val input = EditText(this)
        input.hint = "index.html"
        AlertDialog.Builder(this)
            .setTitle("New HTML File")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val file = File(currentProject, input.text.toString())
                file.writeText("<html><body><h1>${file.name}</h1></body></html>")
                currentFile = file
                loadFiles()
                htmlEditor.setText(file.readText())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun exportProjectAsZip() {
        val zipFile = File(filesDir, "${currentProject.name}.zip")
        val zipOut = ZipOutputStream(FileOutputStream(zipFile))
        currentProject.listFiles()?.forEach {
            zipOut.putNextEntry(ZipEntry(it.name))
            FileInputStream(it).copyTo(zipOut)
            zipOut.closeEntry()
        }
        zipOut.close()
        Toast.makeText(this, "Project ZIP created", Toast.LENGTH_SHORT).show()
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

    /* ===== Syntax Highlight ===== */

    private fun applySyntaxHighlight(editable: Editable) {
        editable.getSpans(0, editable.length, ForegroundColorSpan::class.java)
            .forEach { editable.removeSpan(it) }
        val t = editable.toString()
        highlight("<[^>]+>", t, editable, Color.parseColor("#4CAF50"))
        highlight("\\b(id|class|src|href|style)\\b", t, editable, Color.parseColor("#03A9F4"))
        highlight("\"[^\"]*\"", t, editable, Color.parseColor("#FFC107"))
        highlight("<!--(.|\\n)*?-->", t, editable, Color.GRAY)
        highlight("\\b(function|var|let|const|if|else|return)\\b",
            t, editable, Color.parseColor("#E91E63"))
    }

    private fun highlight(regex: String, text: String, editable: Editable, color: Int) {
        val m = Pattern.compile(regex).matcher(text)
        while (m.find()) {
            editable.setSpan(ForegroundColorSpan(color), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}
