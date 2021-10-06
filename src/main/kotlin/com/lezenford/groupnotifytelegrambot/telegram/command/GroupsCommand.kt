package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.model.repository.GroupRepository
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class GroupsCommand(
    private val groupRepository: GroupRepository
) : BotCommand() {
    override val command: String = "groups"
    override val description: String = "Список всех доступных групп"
    override val publish: Boolean = true

    override suspend fun execute(message: Message): BotApiMethod<*>? {
        return groupRepository.findAllByChatId(message.chatId.toString()).takeIf { it.isNotEmpty() }
            ?.map { it.name }?.sorted()?.joinToString("\n")?.let {
                SendMessage(message.chatId.toString(), "Список доступных групп:\n$it")
            }
            ?: SendMessage(message.chatId.toString(), "Нет активных групп")
    }
}