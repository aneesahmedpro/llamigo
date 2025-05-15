package com.example.llamigo

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.llamigo.ui.theme.LlamigoTheme
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()
        viewModel.viewModelScope.launch {
            val path = File(
                applicationContext.getExternalFilesDir(null),
                "tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf"
            ).path
            viewModel.load(path)
        }

        enableEdgeToEdge()
        setContent {
            LlamigoTheme {
                Scaffold(topBar = { TopBar() }) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .imePadding()
                    ) {
                        val messages by viewModel.messages.collectAsStateWithLifecycle()

                        MainComposable(
                            messages = messages,
                            onSendButtonPressed = { messageBody ->
                                if (messageBody.isNotEmpty()) {
                                    viewModel.sendButtonPressed(messageBody)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

enum class Author {
    ASSISTANT,
    USER
}

data class Message(val author: Author, val body: String)

const val MESSAGE_BUBBLE_EXTRA_MARGIN = 100
const val MESSAGE_BUBBLE_GAP = 10
const val MESSAGE_BUBBLE_RADIUS = 8
const val MESSAGE_BUBBLE_PADDING_VERTICAL = 4
const val MESSAGE_BUBBLE_PADDING_HORIZONTAL = 10
const val SEND_BUTTON_SIZE = 48
const val SEND_BUTTON_ICON_SIZE = 24

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        colors = TopAppBarDefaults.topAppBarColors()
    )
}

@Composable
fun MessageBubble(message: Message) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(
                start = if (message.author == Author.USER) MESSAGE_BUBBLE_EXTRA_MARGIN.dp else MESSAGE_BUBBLE_GAP.dp,
                end = if (message.author == Author.USER) MESSAGE_BUBBLE_GAP.dp else MESSAGE_BUBBLE_EXTRA_MARGIN.dp
            )
    ) {
        Box(
            Modifier
                .align(
                    if (message.author == Author.USER) Alignment.CenterEnd else Alignment.CenterStart
                )
                .clip(AbsoluteRoundedCornerShape(MESSAGE_BUBBLE_RADIUS.dp))
                .background(
                    color =
                        if (message.author == Author.USER)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.tertiaryContainer
                )
        ) {
            Text(
                text = message.body,
                modifier = Modifier
                    .padding(
                        vertical = MESSAGE_BUBBLE_PADDING_VERTICAL.dp,
                        horizontal = MESSAGE_BUBBLE_PADDING_HORIZONTAL.dp
                    ),
                color =
                    if (message.author == Author.USER)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun Conversation(messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = MESSAGE_BUBBLE_GAP.dp,
            alignment = Alignment.Bottom
        ),
        reverseLayout = true
    ) {
        items(messages) { message ->
            MessageBubble(message)
        }
    }
}

val testMessages = listOf(
    Message(Author.USER, "hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny hello honey bunny hello honey bunny"),
    Message(Author.USER, "hello honey bunny hello honey bunny hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny"),
    Message(Author.USER, "hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny hello honey bunny hello honey bunny"),
    Message(Author.USER, "hello honey bunny hello honey bunny hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny"),
    Message(Author.USER, "hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny hello honey bunny hello honey bunny"),
    Message(Author.USER, "hello honey bunny hello honey bunny hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny"),
    Message(Author.USER, "hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny hello honey bunny hello honey bunny"),
    Message(Author.USER, "hello honey bunny hello honey bunny hello honey bunny"),
    Message(Author.ASSISTANT, "hello honey bunny"),
)

@Composable
fun MainComposable(messages: List<Message>, onSendButtonPressed: (String) -> Unit) {
    Column {
        Conversation(
            messages = messages,
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MESSAGE_BUBBLE_GAP.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            var inputText by rememberSaveable { mutableStateOf("Hello") }
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                maxLines = 3,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondaryContainer),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = SEND_BUTTON_SIZE.dp)
                    .verticalScroll(rememberScrollState()),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = AbsoluteRoundedCornerShape(MESSAGE_BUBBLE_RADIUS.dp)
                            )
                            .padding(
                                vertical = MESSAGE_BUBBLE_PADDING_VERTICAL.dp,
                                horizontal = MESSAGE_BUBBLE_PADDING_HORIZONTAL.dp
                            )
                            .wrapContentHeight(Alignment.CenterVertically)
                    ) {
                        innerTextField()
                    }
                }
            )
            Spacer(Modifier.width(MESSAGE_BUBBLE_GAP.dp))
            IconButton(
                onClick = {
                    onSendButtonPressed(inputText)
                    inputText = ""
                },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
                    .width(SEND_BUTTON_SIZE.dp)
                    .height(SEND_BUTTON_SIZE.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .width(SEND_BUTTON_ICON_SIZE.dp)
                        .height(SEND_BUTTON_ICON_SIZE.dp)
                )
            }
        }
    }
}

@Composable
fun ThemedPreview(content: @Composable () -> Unit) {
    LlamigoTheme {
        Surface(content = content)
    }
}

@Preview(
    widthDp = 420,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainPreview() {
    ThemedPreview {
        Column {
            TopBar()
            MainComposable(messages = testMessages, onSendButtonPressed = {})
        }
    }
}