package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.extensions.Logger
import com.lezenford.groupnotifytelegrambot.service.GroupService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class RemoveCommand(
    private val groupService: GroupService
) : UserGroupCommand() {
    override val command: String = "remove"
    override val description: String = "Удалить пользователя из группы уведомлений"
    override val publish: Boolean = true

    override suspend fun execute(message: Message): BotApiMethod<*>? {
        message.group?.also { groupName ->
            groupService.findByNameAndChatId(groupName, message.chatId.toString())?.also { group ->
                (message.usersInfo + message.usersTextLoginInfo).forEach { userInfo ->
                    group.users.removeIf {
                        it.userId == (userInfo.id ?: "") || it.login == (userInfo.login ?: "")
                    }
                }
                withContext(Dispatchers.IO) { groupService.save(group) }
            }
        }
        return SendMessage(message.chatId.toString(), "Команда выполнена успешно").apply { disableNotification = true }
    }

    companion object {
        private val log by Logger()
    }
}