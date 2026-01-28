package com.webcoderpro

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editorTab = findViewById<Button>(R.id.tabEditor)
        val previewTab = findViewById<Button>(R.id.tabPreview)
        val htmlEditor = findViewById<EditText>(R.id.htmlEditor)
        val webView = findViewById<WebView>(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        htmlEditor.setText("""
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; }
                    h1 { color: green; }
                </style>
            </head>
            <body>
                <h1>WebCoderPro 🚀</h1>
                <button onclick="alert('JS Working!')">Click Me</button>
            </body>
            </html>
        """.trimIndent())

        editorTab.setOnClickListener {
            htmlEditor.visibility = View.VISIBLE
            webView.visibility = View.GONE
        }

        previewTab.setOnClickListener {
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
    }
}
