package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.extensions.START_MENTION_SYMBOL
import com.lezenford.groupnotifytelegrambot.service.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class GroupCommand(
    private val userService: UserService
) : BotCommand() {
    override val command: String = "group"
    override val description: String = "Вывести участников конкретной группы"
    override val publish: Boolean = true

    override suspend fun execute(message: Message): BotApiMethod<*>? {
        return message.text.split(" ").getOrNull(1)?.let {
            if (it.startsWith(START_MENTION_SYMBOL).not()) {
                "$START_MENTION_SYMBOL$it"
            } else {
                it
            }
        }?.let {
            userService.findAllUsersByChatIdAndGroupName(message.chatId.toString(), it)
        }?.mapNotNull {
            if (it.name != null) {
                if (it.login != null) {
                    "${it.name} (${it.login})"
                } else {
                    it.name
                }
            } else {
                it.login
            }
        }?.joinToString("\n")
            ?.let { SendMessage(message.chatId.toString(), "Состав группы:\n$it").apply { disableNotification = true } }
    }
}