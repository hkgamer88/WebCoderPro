import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.graphics.Color
import java.util.regex.Pattern
import android.widget.Toast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
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
private fun showFileOptions(file: File) {
    val options = arrayOf("Rename", "Delete")

    AlertDialog.Builder(this)
        .setTitle(file.name)
        .setItems(options) { _, which ->
            when (which) {
                0 -> renameFileDialog(file)
                1 -> deleteFile(file)
            }
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
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                val newFile = File(filesDirPath, newName)
                file.renameTo(newFile)
                loadFiles()
                fileAdapter.notifyDataSetChanged()

                if (currentFile == file) {
                    currentFile = newFile
                    htmlEditor.setText(newFile.readText())
                }
            }
        }
        .setNegativeButton("Cancel", null)
        .show()
}
private fun deleteFile(file: File) {
    AlertDialog.Builder(this)
        .setTitle("Delete File")
        .setMessage("Are you sure?")
        .setPositiveButton("Delete") { _, _ ->
            file.delete()
            loadFiles()
            fileAdapter.notifyDataSetChanged()

            if (currentFile == file) {
                currentFile = null
                htmlEditor.setText("")
            }
        }
        .setNegativeButton("Cancel", null)
        .show()
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
private fun exportProjectAsZip() {
    val zipFile = File(filesDir, "WebCoderPro_Project.zip")

    try {
        val zipOut = ZipOutputStream(FileOutputStream(zipFile))

        filesDirPath.listFiles()?.forEach { file ->
            val entry = ZipEntry(file.name)
            zipOut.putNextEntry(entry)

            val fis = FileInputStream(file)
            fis.copyTo(zipOut)
            fis.close()
            zipOut.closeEntry()
        }

        zipOut.close()

        Toast.makeText(
            this,
            "ZIP created: ${zipFile.absolutePath}",
            Toast.LENGTH_LONG
        ).show()

    } catch (e: Exception) {
        Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show()
    }
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceStateval exportBtn = findViewById<Button>(R.id.btnExport)

exportBtn.setOnClickListener {
    exportProjectAsZip()
})
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
fileList.setOnItemLongClickListener { _, _, position, _ ->
    val selectedFile = File(filesDirPath, fileNames[position])
    showFileOptions(selectedFile)
    true
}
newFileBtn.setOnClickListener {
    createNewFileDialog()
}
        val editorTab = findViewById<Button>(R.id.tabEditor)
        val previewTab = findViewById<Button>(R.id.tabPreview)
        val darkBtn = findViewById<Button>(R.id.btnDark)
        val savedInstanceStatenceStatenceState = findViewById<EditText>(R.id.htmlEditor)
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
