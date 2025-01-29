package edu.skku.cs.quizifier

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizExtractActivity : AppCompatActivity() {

    private lateinit var textQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var quiz: Quiz
    private lateinit var quizManager: QuizManager
    private lateinit var quizGeneratorModel: QuizGeneratorModel
    private lateinit var btnRestartQuiz: Button
    private lateinit var btnSaveQuiz : Button
    private lateinit var btnGradeQuiz : Button
    private lateinit var progressBarContainer: ConstraintLayout
    private lateinit var quizContainer: ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_extract)

        val myApplication = application as MyApplication
        quizManager = myApplication.quizManager
        btnRestartQuiz = findViewById<Button>(R.id.btnRestartQuiz)
        btnSaveQuiz = findViewById<Button>(R.id.btnSaveQuiz)
        btnGradeQuiz = findViewById<Button>(R.id.btnGradeQuiz)

        textQuestion = findViewById(R.id.textQuestion)
        radioGroup = findViewById(R.id.radioGroup)

        progressBarContainer = findViewById(R.id.progressBarContainer)
        quizContainer = findViewById(R.id.quizContainer)


        val currentPageText: String? = intent.getStringExtra("currentPageText")

        if (currentPageText != null) {
            CoroutineScope(Dispatchers.Main).launch {
                createQuizFromText(currentPageText)
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_message_when_page_text_null), Toast.LENGTH_SHORT).show()
            this.finish()
        }
        quizGeneratorModel = QuizGeneratorModel()

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                val selectedRadioButton: RadioButton = findViewById(checkedId)
                val selectedOption: String = selectedRadioButton.text.toString()
            }
        }

        btnRestartQuiz.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (currentPageText != null) {
                    createQuizFromText(currentPageText)
                }
            }
        }

        btnSaveQuiz.setOnClickListener {
            quizManager.saveQuiz(quiz)
            Toast.makeText(this, getString(R.string.toast_message_message_when_quiz_saved), Toast.LENGTH_SHORT).show()
        }

        btnGradeQuiz.setOnClickListener {
        }

        btnGradeQuiz.setOnClickListener {
            Log.d("quizzes: ", quizGeneratorModel.quizStorage.joinToString(separator = "\n"))
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedOption = selectedRadioButton.text.toString()

                handleSelectedOption(selectedOption)

            } else {
                Toast.makeText(this, getString(R.string.toast_message_when_no_option_selected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun waiting() {
        progressBarContainer.visibility = View.VISIBLE

        radioGroup.isClickable = false
        btnGradeQuiz.isClickable = false
        btnRestartQuiz.isClickable = false
        btnSaveQuiz.isClickable = false

        quizContainer.visibility = View.GONE
    }

    private fun stopWaiting() {

        progressBarContainer.visibility = View.GONE

        radioGroup.isClickable = true
        btnGradeQuiz.isClickable = true
        btnRestartQuiz.isClickable = true
        btnSaveQuiz.isClickable = true

        quizContainer.visibility = View.VISIBLE
    }

    private suspend fun createQuizFromText(text: String) {
        Log.d("pdf text: ", text)
        textQuestion.text = getString(R.string.textview_message_while_loading)
        radioGroup.clearCheck()
        waiting()

        clearRadioButtonText()

        val generatedQuiz = quizGeneratorModel.generateQuizFromText(text)
        if (generatedQuiz != null) {
            quiz = generatedQuiz
            textQuestion.text = quiz.question

            setRadioButtonText(quiz.options)
            stopWaiting()
        }
    }

    private fun clearRadioButtonText() {
        for (i in 0 until radioGroup.childCount) {
            val view = radioGroup.getChildAt(i)
            if (view is RadioButton) {
                view.text = ""
            }
        }
    }

    private fun setRadioButtonText(options: List<String>) {
        var optionIndex = 0
        for (i in 0 until radioGroup.childCount) {
            val view = radioGroup.getChildAt(i)
            if (view is RadioButton) {
                view.text = options[optionIndex]
                optionIndex++
            }
        }
    }

    private fun handleSelectedOption(option: String) {
        val selectedOptionIndex = quiz.options.indexOf(option) + 1
        if (selectedOptionIndex == quiz.answer) {
            Toast.makeText(this, getString(R.string.toast_message_when_answer_right), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.toast_message_when_answer_wrong), Toast.LENGTH_SHORT).show()
        }
    }
}