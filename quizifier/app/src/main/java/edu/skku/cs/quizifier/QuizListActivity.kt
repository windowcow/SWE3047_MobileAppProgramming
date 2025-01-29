package edu.skku.cs.quizifier

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuizListActivity : AppCompatActivity() {
    private lateinit var quizManager: QuizManager
    private lateinit var quizAdapter: QuizAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        quizManager = QuizManager(this)
        quizAdapter = QuizAdapter()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = quizAdapter

        val quizzes = quizManager.loadQuizzes()
        quizAdapter.setQuizzes(quizzes)
    }
}
