package edu.skku.cs.quizifier

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*


class QuizReviewActivity : AppCompatActivity() {

    private lateinit var textQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var quiz: Quiz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_review)

        textQuestion = findViewById(R.id.textQuestion)
        radioGroup = findViewById(R.id.radioGroup)

        val question: String? = intent.getStringExtra("question")
        val options: List<String>? = intent.getStringArrayListExtra("options")
        val answer: Int? = intent.getIntExtra("answer", -1)

        if (question != null && options != null && answer != null) {
            quiz = Quiz(question, options, answer)
            displayQuiz()
        }

        val btnGradeQuiz: Button = findViewById(R.id.btnGradeQuiz)
        btnGradeQuiz.setOnClickListener {
            checkAnswer()
        }
    }

    private fun displayQuiz() {
        textQuestion.text = quiz.question

        radioGroup.clearCheck()
        setRadioButtonText(quiz.options)
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

    private fun checkAnswer() {
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        if (selectedRadioButtonId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
            val selectedOption = selectedRadioButton.text.toString()
            val selectedOptionIndex = quiz.options.indexOf(selectedOption)

            if (selectedOptionIndex + 1 == quiz.answer) {
                Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "오답입니다!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "답을 선택해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
