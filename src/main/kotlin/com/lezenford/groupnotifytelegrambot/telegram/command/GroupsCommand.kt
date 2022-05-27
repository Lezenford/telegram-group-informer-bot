package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.service.GroupService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class GroupsCommand(
    private val groupService: GroupService
) : BotCommand() {
    override val command: String = "groups"
    override val description: String = "Список всех доступных групп"
    override val publish: Boolean = true

    override suspend fun execute(message: Message): BotApiMethod<*>? {
        return groupService.findAllByChatId(message.chatId.toString()).takeIf { it.isNotEmpty() }
            ?.map { it.name }?.sorted()?.joinToString("\n")?.let {
                SendMessage(message.chatId.toString(), "Список доступных групп:\n$it").apply {
                    disableNotification = true
                }
            }
            ?: SendMessage(message.chatId.toString(), "Нет активных групп").apply { disableNotification = true }
    }
}