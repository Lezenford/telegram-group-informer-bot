package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.service.GroupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class RemoveAllCommand(
    private val groupService: GroupService
) : UserGroupCommand() {
    override val command: String = "removeall"
    override val description: String = "Удалить пользователя из всех групп уведомлений"
    override val publish: Boolean = true

    override suspend fun execute(message: Message): BotApiMethod<*>? {
        val usersInfo = message.usersInfo + message.usersTextLoginInfo
        groupService.findAllByChatId(message.chatId.toString()).onEach { group ->
            usersInfo.forEach { userInfo ->
                group.users.removeIf { it.userId == (userInfo.id ?: "") || it.login == (userInfo.login ?: "") }
            }
        }.also { withContext(Dispatchers.IO) { groupService.saveAll(it) } }
        return SendMessage(message.chatId.toString(), "Команда выполнена успешно").apply { disableNotification = true }
    }
}