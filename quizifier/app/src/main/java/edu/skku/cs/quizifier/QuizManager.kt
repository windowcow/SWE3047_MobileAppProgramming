package edu.skku.cs.quizifier

import android.content.ContentValues
import android.content.Context

import android.database.Cursor

class QuizManager(context: Context) {

    private val quizDatabaseHelper: QuizDatabaseHelper = QuizDatabaseHelper(context)

    fun saveQuiz(quiz: Quiz) {
        val db = quizDatabaseHelper.writableDatabase

        val values = ContentValues()
        values.put(QuizDatabaseHelper.COLUMN_QUESTION, quiz.question)
        values.put(QuizDatabaseHelper.COLUMN_OPTIONS, quiz.options.joinToString(","))
        values.put(QuizDatabaseHelper.COLUMN_ANSWER, quiz.answer)

        db.insert(QuizDatabaseHelper.TABLE_QUIZ, null, values)

        db.close()
    }

    fun loadQuizzes(): List<Quiz> {
        val db = quizDatabaseHelper.readableDatabase

        val quizzes: MutableList<Quiz> = mutableListOf()

        val projection = arrayOf(
            QuizDatabaseHelper.COLUMN_QUESTION,
            QuizDatabaseHelper.COLUMN_OPTIONS,
            QuizDatabaseHelper.COLUMN_ANSWER
        )

        val cursor: Cursor? = db.query(
            QuizDatabaseHelper.TABLE_QUIZ,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val question = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_QUESTION))
                val optionsString = cursor.getString(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_OPTIONS))
                val answer = cursor.getInt(cursor.getColumnIndexOrThrow(QuizDatabaseHelper.COLUMN_ANSWER))

                val options = optionsString.split(",")

                val quiz = Quiz(question, options, answer)
                quizzes.add(quiz)
            }
        }

        db.close()

        return quizzes
    }
}
