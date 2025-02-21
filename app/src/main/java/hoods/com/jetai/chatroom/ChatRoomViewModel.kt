package hoods.com.jetai.chatroom

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import hoods.com.jetai.data.models.ChatMessage
import hoods.com.jetai.data.models.ChatRoom
import hoods.com.jetai.data.repository.AuthRepository
import hoods.com.jetai.data.repository.ChatRepository
import hoods.com.jetai.utils.ext.collectAndHandle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatRoomViewModel(
    private val chatRepository: ChatRepository = hoods.com.jetai.Graph.chatRepository,
    private val authRepository : AuthRepository = hoods.com.jetai.Graph.authRepository

)

    : ViewModel() {

    var chatRoomState by mutableStateOf(ChatRoomState())
        private set

    init {
        viewModelScope.launch {
            authRepository.currentUser.collectLatest {
                chatRoomState= chatRoomState.copy(
                    currentUser = it
                )
            }
        }



    }

    fun initChatRoom() {
        viewModelScope.launch {
            chatRepository.getChatRoomList().collectAndHandle(
                onError = {
                          chatRoomState=chatRoomState.copy(errorMessage = it?.message, loading = false)
                },
                onLoading = {
                    chatRoomState=chatRoomState.copy(errorMessage = null, loading = true)
                }
            ) {chatRooms->
                chatRoomState=chatRoomState.copy(errorMessage = null, loading = false, chatRooms = chatRooms)
            }
        }



    }

    fun resetChatId(){
        chatRoomState= chatRoomState.copy(newChatId = null)
    }

    fun newChatRoom(){
        viewModelScope.launch {
            val chatId=chatRepository.createChatRoom()
            chatRoomState = chatRoomState.copy(newChatId = chatId)
        }

    }

    fun deleteChatRoom(chatId: String){
        viewModelScope.launch {

            chatRepository.deleteChat(chatId)
            resetChatId()
        }

    }


}

data class ChatRoomState(
    val chatRooms: List<ChatRoom> = emptyList(),
    val loading: Boolean= false,
    val errorMessage: String? = null,
    val newChatId: String?= null,
    val currentUser: FirebaseUser? = null
){

}