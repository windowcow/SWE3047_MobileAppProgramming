package edu.skku.cs.quizifier

class QuizParser {
    fun parseQuizText(quizText: String): List<Quiz> {
        val quizList = mutableListOf<Quiz>()

        val quizTexts = quizText.split("\n\n")
        for (quizTextItem in quizTexts) {
            val quiz = parseSingleQuiz(quizTextItem)
            if (quiz != null) {
                quizList.add(quiz)
            }
        }

        return quizList
    }

    private fun parseSingleQuiz(quizText: String): Quiz? {
        val question = extractValue(quizText, "[q]:")
        val option1 = extractValue(quizText, "[o1]:")
        val option2 = extractValue(quizText, "[o2]:")
        val option3 = extractValue(quizText, "[o3]:")
        val option4 = extractValue(quizText, "[o4]:")
        val answer = extractValue(quizText, "[a]:")?.toIntOrNull() ?: 0

        val options = listOf(option1, option2, option3, option4)

        return if (question.isNotEmpty() && options.size == 4) {
            Quiz(question, options, answer)
        } else {
            null
        }
    }

    private fun extractValue(text: String, prefix: String): String {
        val startIndex = text.indexOf(prefix)
        if (startIndex == -1) return ""

        val endIndex = text.indexOf("\n", startIndex)
        if (endIndex == -1) return ""
        val value = text.substring(startIndex + prefix.length, endIndex).trim()
        return value
    }
}
