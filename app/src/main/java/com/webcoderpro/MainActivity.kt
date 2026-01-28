import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var isDark = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editorTab = findViewById<Button>(R.id.tabEditor)
        val previewTab = findViewById<Button>(R.id.tabPreview)
        val darkBtn = findViewById<Button>(R.id.btnDark)
        val htmlEditor = findViewById<EditText>(R.id.htmlEditor)
        val webView = findViewById<WebView>(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        htmlEditor.setText("""
            <html>
            <head>
                <style>
                    body { font-family: sans-serif; padding:16px; }
                    h1 { color: #4CAF50; }
                </style>
            </head>
            <body>
                <h1>WebCoderPro 🚀</h1>
                <p>Dark mode ready</p>
                <script>
                    console.log("JS working");
                </script>
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

        darkBtn.setOnClickListener {
            isDark = !isDark
            applyTheme(isDark, htmlEditor, webView)
        }
    }

    private fun applyTheme(dark: Boolean, editor: EditText, webView: WebView) {
        if (dark) {
            editor.setBackgroundColor(Color.parseColor("#121212"))
            editor.setTextColor(Color.WHITE)
            editor.setHintTextColor(Color.LTGRAY)

            val darkHtml = """
                <style>
                    body { background:#121212; color:#ffffff; }
                </style>
            """ + editor.text.toString()

            webView.loadDataWithBaseURL(
                null, darkHtml, "text/html", "UTF-8", null
            )
        } else {
            editor.setBackgroundColor(Color.WHITE)
            editor.setTextColor(Color.BLACK)
            editor.setHintTextColor(Color.DKGRAY)

            webView.loadDataWithBaseURL(
                null, editor.text.toString(), "text/html", "UTF-8", null
            )
        }
    }
}
