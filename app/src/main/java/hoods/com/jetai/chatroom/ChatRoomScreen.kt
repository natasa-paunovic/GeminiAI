@file:OptIn(ExperimentalMaterial3Api::class)

package hoods.com.jetai.chatroom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hoods.com.jetai.authentication.register.defaultPadding
import hoods.com.jetai.authentication.register.itemSpacing
import hoods.com.jetai.data.models.ChatRoom
import hoods.com.jetai.utils.ext.formatDate
import kotlinx.coroutines.launch


const val EMPTY_TITLE = "empty_title"

@Composable
fun ChatRoomScreen(
    modifier: Modifier = Modifier,
    chatRoomViewModel: ChatRoomViewModel = viewModel(),
    onNavigateToMsgScreen: (id: String, chatTitle: String) -> Unit
) {

    val chatRoomState: ChatRoomState = chatRoomViewModel.chatRoomState
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        chatRoomViewModel.initChatRoom()

    }

    LaunchedEffect(chatRoomState.newChatId) {
        if (chatRoomState.newChatId != null) {
            onNavigateToMsgScreen(chatRoomState.newChatId, EMPTY_TITLE)
            chatRoomViewModel.resetChatId()
        }


    }
    LazyColumn(modifier = modifier) {
        item {
            AnimatedVisibility(visible = chatRoomState.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            }

        }
        items(chatRoomState.chatRooms) {it->
            ChatRoomItem(chatRoom = it,
                onClick = {
                    onNavigateToMsgScreen(it.id, it.title)
                },
                onDeleteClick = {
                    coroutineScope.launch {
                        chatRoomViewModel.deleteChatRoom(it.id)
                    }

                }
            )



            Spacer(Modifier.height(itemSpacing))

        }


    }
}


@Composable
fun ChatRoomItem(
    modifier: Modifier = Modifier,
    chatRoom: ChatRoom,
    onClick: () -> Unit,
    onDeleteClick:()-> Unit
) {

    ElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {

        Box(modifier = Modifier.fillMaxWidth()) { // Use Box to align elements

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = defaultPadding, end = defaultPadding, top = 16.dp, bottom = 16.dp) // Adjust padding
            ) {
                Text(
                    chatRoom.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(itemSpacing))

                Text(
                    formatDate(chatRoom.timestamp.toDate()),
                    fontWeight = FontWeight.Light
                )
            }



            // Icon in the top-right corner
            IconButton(
                onClick = onDeleteClick, // Handle icon click
                modifier = Modifier
                    .align(Alignment.CenterEnd) // Align to top right corner
                    .padding(8.dp) // Add padding around the icon
            ) {
                Icon(
                    imageVector = Icons.Default.Delete, // Use any icon here
                    contentDescription = "Delete"
                )
            }
        }

    }

}