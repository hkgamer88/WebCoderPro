import android.app.AlertDialog
import android.widget.ArrayAdapter
import java.io.File
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
private lateinit var filesDirPath: File
private lateinit var fileAdapter: ArrayAdapter<String>
private val fileNames = mutableListOf<String>()
private var currentFile: File? = null
    private var isDark = false
private fun loadFiles() {
    fileNames.clear()
    filesDirPath.listFiles()?.forEach {
        fileNames.add(it.name)
    }
}

private fun createNewFileDialog() {
    val input = EditText(this)
    input.hint = "index.html"

    AlertDialog.Builder(this)
        .setTitle("New HTML File")
        .setView(input)
        .setPositiveButton("Create") { _, _ ->
            val name = input.text.toString()
            if (name.isNotEmpty()) {
                val file = File(filesDirPath, name)
                file.writeText("<html><body><h1>$name</h1></body></html>")
                currentFile = file
                loadFiles()
                fileAdapter.notifyDataSetChanged()
                htmlEditor.setText(file.readText())
            }
        }
        .setNegativeButton("Cancel", null)
        .show()
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
filesDirPath = File(filesDir, "projects")
if (!filesDirPath.exists()) filesDirPath.mkdir()

val fileList = findViewById<ListView>(R.id.fileList)
val newFileBtn = findViewById<Button>(R.id.btnNewFile)

loadFiles()

fileAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames)
fileList.adapter = fileAdapter

fileList.setOnItemClickListener { _, _, position, _ ->
    val file = File(filesDirPath, fileNames[position])
    currentFile = file
    htmlEditor.setText(file.readText())
}

newFileBtn.setOnClickListener {
    createNewFileDialog()
}
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

        previewTab.setOnClickListener {currentFile?.writeText(htmlEditor.text.toString())}
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
