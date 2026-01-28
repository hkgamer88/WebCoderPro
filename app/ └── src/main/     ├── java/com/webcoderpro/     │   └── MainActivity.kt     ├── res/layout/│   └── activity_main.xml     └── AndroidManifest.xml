
package com.webcoderpro

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val htmlEditor = findViewById<EditText>(R.id.htmlEditor)
        val runButton = findViewById<Button>(R.id.runButton)
        val webView = findViewById<WebView>(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        htmlEditor.setText("""
            <html>
            <body>
                <h1>Hello WebCoderPro 🚀</h1>
                <p>Live Preview Working</p>
            </body>
            </html>
        """.trimIndent())

        runButton.setOnClickListener {
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
