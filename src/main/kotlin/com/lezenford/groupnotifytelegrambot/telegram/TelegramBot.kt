package com.lezenford.groupnotifytelegrambot.telegram

import com.lezenford.groupnotifytelegrambot.configuration.properties.TelegramProperties
import com.lezenford.groupnotifytelegrambot.extensions.Logger
import com.lezenford.groupnotifytelegrambot.extensions.START_MENTION_SYMBOL
import com.lezenford.groupnotifytelegrambot.extensions.removeMention
import com.lezenford.groupnotifytelegrambot.service.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import java.io.Serializable
import kotlin.system.exitProcess

@Component
class TelegramBot(
    private val commandHandler: CommandHandler,
    private val telegramProperties: TelegramProperties,
    private val webClient: WebClient,
    private val userService: UserService
) {
    private val url = "https://api.telegram.org/bot${telegramProperties.token}/"

    @EventListener(ApplicationStartedEvent::class)
    fun init() = CoroutineScope(Dispatchers.Default).launch {
        sendMessage(SetWebhook(telegramProperties.path + telegramProperties.token))
            ?: kotlin.run {
                log.error("Webhook is not installed!")
                exitProcess(0)
            }
        sendMessage(SetMyCommands().apply {
            commands = commandHandler.publicCommands().map {
                BotCommand(it.command, it.description)
            }
        }) ?: log.error("Error set bot commands")
    }

    suspend fun receive(update: Update): BotApiMethod<*>? {
        return kotlin.runCatching {
            update.message?.run {
                processDataUpdate(update.message)
                if (isCommand) {
                    commandHandler.execute(update)
                } else {
                    prepareNotification(update.message)
                }
            }
        }.onFailure {
            log.error("Update executed with exception. Update: $update", it)
        }.getOrNull()
    }

    suspend fun <T : Serializable> sendMessage(message: BotApiMethod<T>): T? {
        return kotlin.runCatching {
            val result = webClient.post()
                .uri("$url${message.method}")
                .bodyValue(message)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBodyOrNull<String>()
            kotlin.runCatching {
                message.deserializeResponse(result)
            }.onFailure {
                log.error("Incorrect telegram api invoke: $result")
            }.getOrThrow()
        }.onFailure {
            log.error(it)
        }.getOrNull()
    }

    private suspend fun prepareNotification(message: Message): SendMessage? {
        return message.takeIf { it.text?.contains(START_MENTION_SYMBOL) ?: false }?.run {
            text.split(" ", "\n").filter { it.startsWith(START_MENTION_SYMBOL) }
                .map { it.replace(Regex("[^A-Za-z0-9_@\n]"), "") }.flatMap {
                    userService.findAllUsersByChatIdAndGroupName(message.chatId.toString(), it)
                }.toSet().mapNotNull { user ->
                    if (user.userId != null && user.name != null) {
                        "[${user.name}](tg://user?id=${user.userId})"
                    } else {
                        user.login?.run { "@${this.map { if (it.code in 1..125) "\\$it" else it }.joinToString("")}" }
                    }
                }.joinToString(", ").takeIf { it.isNotEmpty() }
                ?.let {
                    SendMessage(chatId.toString(), it).apply {
                        parseMode = "MarkdownV2"
                        replyToMessageId = message.messageId
                    }
                }
        }
    }

    private suspend fun processDataUpdate(message: Message) {
        userService.findUser(message.from.id.toString(), message.from.userName)?.also {
            if (it.login != message.from.userName || it.name != message.from.firstName) {
                it.userId = message.from.id.toString()
                it.name = message.from.firstName
                it.login = message.from.userName?.removeMention()
                userService.save(it)
            }
        }
    }

    companion object {
        private val log by Logger()
    }
}