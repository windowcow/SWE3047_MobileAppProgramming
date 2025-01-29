package edu.skku.cs.quizifier

data class Quiz(
    val question: String,
    val options: List<String>,
    val answer: Int
){
    override fun toString(): String {
        return "Question: $question, Options: $options, Answer: $answer"
    }
}
