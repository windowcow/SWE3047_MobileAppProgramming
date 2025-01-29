package edu.skku.cs.quizifier

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class QuizGeneratorModel {
    private val apiKey: String = "OPENAI APIKEY"
    private val openAI: OpenAI = OpenAI(token = apiKey, timeout = Timeout(socket = 300.seconds))
    private val systemPrompt = """
        너는 다음 내용에서 아주 적절한 객관식 퀴즈를 만들어야해. 
        시험에 가장 나올 것 같은 키워드나 중요한  내용을 퀴즈로 추출해야 해. 
        선택지는 4개로 구성되며 하나는 옳은 선택지이고 나머지는 틀린 선택지어야 해. 
        
        [q]: 다음에는 한국어로 된 문제를 알려줘야 해. 문제는 텍스트에서 가장 중요한 내용을 담아야 해.
        [o1]: 다음에는 선택지1이 위치해야해
        [o2]: 다음에는 선택지2가 위치해야해
        [o3]: 다음에는 선택지3이 위치해야해
        [o4]: 다음에는 선택지4가 위치해야해
        [a]: 다음에는 문제의 정답을 알려줘야해. 
        
        예를 들어 다음과 같이 10개의 퀴즈를 만들어줘야해. 다음과 같이 한 줄씩 띄어가면서.
        
        [q]: 커피를 마신 날, 어떤 것이 증가하고 감소했는가?
        [o1]: 식욕과 수분섭취량
        [o2]: 신체활동량과 수면시간
        [o3]: 혈압과 심장박동수
        [o4]: 체온과 대사량
        [a]: 2
        
        [q]: 커피를 마신 날, 어떤 것이 증가하고 감소했는가?
        [o1]: 식욕과 수분섭취량
        [o2]: 신체활동량과 수면시간
        [o3]: 혈압과 심장박동수
        [o4]: 체온과 대사량
        [a]: 2
        
        [q]: 커피를 마신 날, 어떤 것이 증가하고 감소했는가?
        [o1]: 식욕과 수분섭취량
        [o2]: 신체활동량과 수면시간
        [o3]: 혈압과 심장박동수
        [o4]: 체온과 대사량
        [a]: 2
        
        이런 식으로 한국어로 된 퀴즈 10개를 작성해줘
    """.trimIndent()
    val quizStorage: MutableList<Quiz> = mutableListOf()

    @OptIn(BetaOpenAI::class)
    suspend fun generateQuizFromText(text: String): Quiz {
        if (quizStorage.size >= 1) {
            return quizStorage.removeFirst()
        } else {
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.System,
                        content = systemPrompt
                    ),
                    ChatMessage(
                        role = ChatRole.User,
                        content = text
                    )
                )
            )

            Log.d("generated1: ", chatCompletionRequest.toString())

            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            Log.d("generated2: ", completion.toString())
            val generatedText = completion.choices.firstOrNull()?.message?.content ?: ""
            val quizzes = parseGeneratedText(generatedText)
            quizStorage.addAll(quizzes)

            return quizStorage.removeFirst()
        }


    }


    private fun parseGeneratedText(text: String): List<Quiz> {
        val quizzes = mutableListOf<Quiz>()

        val quizBlocks = text.split("\n\n")
        quizBlocks.forEach { block ->
            val question = extractValue(block, "[q]:")
            val option1 = extractValue(block, "[o1]:")
            val option2 = extractValue(block, "[o2]:")
            val option3 = extractValue(block, "[o3]:")
            val option4 = extractValue(block, "[o4]:")

            val answerText = extractAnswer(block, "[a]:")?.trim()
            val answer = answerText?.toIntOrNull() ?: 0

            val options = listOf(option1, option2, option3, option4)

            quizzes.add(Quiz(question, options, answer))
        }

        return quizzes
    }


    private fun extractValue(text: String, prefix: String): String {
        val startIndex = text.indexOf(prefix)
        if (startIndex == -1) return ""

        val endIndex = text.indexOf("\n", startIndex)
        if (endIndex == -1) return ""
        val value = text.substring(startIndex + prefix.length, endIndex).trim()
        return value
    }

    private fun extractAnswer(text: String, prefix: String): String {
        val startIndex = text.indexOf(prefix)
        if (startIndex == -1) return ""

        val postPrefixIndex = startIndex + prefix.length
        val postSpaceIndex = text.indexOf(" ", postPrefixIndex)
        if (postSpaceIndex == -1) return ""

        val value = text.substring(postSpaceIndex + 1, postSpaceIndex + 2)
        return value
    }

}