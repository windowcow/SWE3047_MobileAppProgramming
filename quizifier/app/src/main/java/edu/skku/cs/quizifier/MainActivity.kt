package edu.skku.cs.quizifier
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anggrayudi.storage.SimpleStorageHelper
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst

class MainActivity : AppCompatActivity() {

    private val storageHelper = SimpleStorageHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectFileButton = findViewById<Button>(R.id.selectFileButton)
        selectFileButton.setOnClickListener {
            storageHelper.openFilePicker()
        }

        val viewQuizButton = findViewById<Button>(R.id.viewQuizButton)
        viewQuizButton.setOnClickListener {
            openQuizListActivity()
        }

        storageHelper.onFileSelected = { requestCode, files ->
            Log.d("123", files[0].name.toString())
            val intent = Intent(this, PdfViewerActivity::class.java)
            intent.putExtra("fileUri", files[0].uri)
            startActivity(intent)
        }
    }

    private fun openQuizListActivity() {
        val intent = Intent(this, QuizListActivity::class.java)
        startActivity(intent)
    }

}