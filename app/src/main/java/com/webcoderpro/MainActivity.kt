package com.webcoderpro

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var editor: EditText
    private lateinit var webView: WebView
    private lateinit var htmlFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editor = findViewById(R.id.editor)
        webView = findViewById(R.id.webView)

        val btnEditor = findViewById<Button>(R.id.btnEditor)
        val btnPreview = findViewById<Button>(R.id.btnPreview)
        val btnSave = findViewById<Button>(R.id.btnSave)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // File setup
        htmlFile = File(filesDir, "index.html")
        if (!htmlFile.exists()) {
            htmlFile.writeText(defaultHtml())
        }

        editor.setText(htmlFile.readText())

        btnEditor.setOnClickListener {
            editor.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }

        btnPreview.setOnClickListener {
            saveFile()
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

        btnSave.setOnClickListener {
            saveFile()
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFile() {
        htmlFile.writeText(editor.text.toString())
    }

    private fun defaultHtml(): String {
        return """
            <html>
            <head>
                <title>WebCoderPro</title>
            </head>
            <body>
                <h1>Hello WebCoderPro 🚀</h1>
                <p>Edit HTML and preview instantly.</p>
                <button onclick="alert('JS works!')">Test JS</button>
            </body>
            </html>
        """.trimIndent()
    }
}
