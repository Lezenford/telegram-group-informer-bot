package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.extensions.Logger
import com.lezenford.groupnotifytelegrambot.model.entity.Group
import com.lezenford.groupnotifytelegrambot.model.entity.TelegramUser
import com.lezenford.groupnotifytelegrambot.service.GroupService
import com.lezenford.groupnotifytelegrambot.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class AddCommand(
    private val groupService: GroupService,
    private val userService: UserService
) : UserGroupCommand() {
    override val command: String = "add"
    override val description: String = "Добавить пользователя в группу уведомлений"
    override val publish: Boolean = true

    override suspend fun execute(message: Message): BotApiMethod<*>? {
        message.group?.also {
            addToGroup(message.chat.id.toString(), it, message.usersInfo)
        }
        return SendMessage(message.chatId.toString(), "Команда выполнена успешно").apply { disableNotification = true }
    }

    private suspend fun addToGroup(
        chatId: String,
        groupName: String,
        usersInfo: Set<UserInfo>
    ) {
        val group: Group = groupService.findByNameAndChatId(groupName, chatId)
            ?: withContext(Dispatchers.IO) { groupService.save(Group(name = groupName, chatId = chatId)) }

        usersInfo.takeIf { it.isNotEmpty() }?.forEach { userInfo ->
            val user = userService.findUser(userInfo.id ?: "", userInfo.login ?: "")
                ?: TelegramUser(userId = userInfo.id, name = userInfo.name, login = userInfo.login)
                    .let { withContext(Dispatchers.IO) { userService.save(it) } }
            group.users.add(user)
        }?.also { withContext(Dispatchers.IO) { groupService.save(group) } }
    }

    companion object {
        private val log by Logger()
    }
}