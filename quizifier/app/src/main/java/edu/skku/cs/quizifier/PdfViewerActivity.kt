package edu.skku.cs.quizifier

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView

import com.github.barteksc.pdfviewer.PDFView
import java.io.File
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor


class PdfViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)
        val pdfView = findViewById<PDFView>(R.id.pdfView)
        val btnMakeQuiz = findViewById<Button>(R.id.btnMakeQuiz)
        val tvCurrentPage = findViewById<TextView>(R.id.tvCurrentPage)
        var currentPageIndex = 0

        val fileUri: Uri? = intent.getParcelableExtra("fileUri")
        fileUri?.let {
            pdfView.fromUri(it)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange { page, pageCount ->
                    tvCurrentPage.text = "Current page: ${page + 1}p"  // 페이지 번호는 0부터 시작하므로 1을 더해줍니다.
                    currentPageIndex = page
                }
                .load()
        }


        fun extractTextFromPdf(context: Context, uri: Uri, pageNumber: Int): String {
            val inputStream = context.contentResolver.openInputStream(uri)
            val pdfReader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(pdfReader)
            val page = pdfDocument.getPage(pageNumber)

            val result = PdfTextExtractor.getTextFromPage(page)

            pdfDocument.close()
            pdfReader.close()

            return result
        }

        fun getRealPathFromUri(context: Context, uri: Uri): String? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri.toString()
            } else {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        cursor.getString(columnIndex)
                    } else {
                        null
                    }
                }
            }
        }

        fun extractTextOrEmpty(context: Context, fileUri: Uri, pageNumber: Int): String {
            val realPath = getRealPathFromUri(context, fileUri) ?: return ""
            return extractTextFromPdf(context, fileUri, pageNumber)
        }

        fun openQuizActivity(text: String) {
            val intent = Intent(this, QuizExtractActivity::class.java).apply {
                putExtra("currentPageText", text)
            }
            startActivity(intent)
        }
        fun makeQuiz(context: Context, fileUri: Uri, currentPage: Int) {
            val extractedText = extractTextOrEmpty(context, fileUri, currentPage)
            openQuizActivity(extractedText)
        }
        btnMakeQuiz.setOnClickListener {
            fileUri?.let { makeQuiz(this, it, currentPageIndex) }
        }

    }
}
