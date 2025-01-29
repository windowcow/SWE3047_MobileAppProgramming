package edu.skku.cs.quizifier

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class QuizAdapter : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    private val quizList: MutableList<Quiz> = mutableListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizList[position]
        holder.bind(quiz)
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    fun setQuizzes(quizzes: List<Quiz>) {
        quizList.clear()
        quizList.addAll(quizzes)
        notifyDataSetChanged()
    }

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnOpenQuiz: Button = itemView.findViewById(R.id.btnOpenQuiz)

        fun bind(quiz: Quiz) {
            btnOpenQuiz.text = quiz.question

            btnOpenQuiz.setOnClickListener {
                val intent = Intent(context, QuizReviewActivity::class.java)
                intent.putExtra("question", quiz.question)
                intent.putStringArrayListExtra("options", ArrayList(quiz.options))
                intent.putExtra("answer", quiz.answer)
                context.startActivity(intent)
            }
        }
    }
}

