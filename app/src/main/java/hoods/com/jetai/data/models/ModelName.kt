package hoods.com.jetai.data.models

import com.google.firebase.Timestamp
import java.util.UUID

enum class ModelName(val modelName:String) {
    TEXT("gemini-pro"),
    MULTIMODAL("gemini-1.5-flash")
}

enum class Participant{
    USER, MODEL, ERROR
}

data class ChatMessage(
    val id:String=UUID.randomUUID().toString(),
    val text:String="",
    val participant: Participant=Participant.USER,
    val timestamp: Timestamp=Timestamp.now()
)

data class ChatRoom(
    val id: String="",
    val title:String="New chat",
    val timestamp: Timestamp=Timestamp.now(),
    val userId:String=""
)