package edu.skku.cs.quizifier

import android.app.Application


class MyApplication : Application() {

    lateinit var quizManager: QuizManager

    override fun onCreate() {
        super.onCreate()

        quizManager = QuizManager(this)
    }
}
