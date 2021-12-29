package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.configuration.CacheConfiguration
import com.lezenford.groupnotifytelegrambot.extensions.Logger
import com.lezenford.groupnotifytelegrambot.model.entity.Group
import com.lezenford.groupnotifytelegrambot.model.entity.TelegramUser
import com.lezenford.groupnotifytelegrambot.model.repository.GroupRepository
import com.lezenford.groupnotifytelegrambot.service.UserService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class AddCommand(
    private val groupRepository: GroupRepository,
    private val userService: UserService
) : UserGroupCommand() {
    override val command: String = "add"
    override val description: String = "Добавить пользователя в группу уведомлений"
    override val publish: Boolean = true

    @Transactional
    @CacheEvict(CacheConfiguration.TELEGRAM_USER_CACHE, allEntries = true)
    override suspend fun execute(message: Message): BotApiMethod<*>? {
        message.group?.also {
            addToGroup(message.chat.id.toString(), it, message.usersInfo)
        }
        return SendMessage(message.chatId.toString(), "Команда выполнена успешно").apply { disableNotification = true }
    }

    private suspend fun addToGroup(
        chatId: String,
        groupName: String,
        usersInfo: List<UserInfo>
    ) {
        val group = groupRepository.findByNameAndChatId(groupName, chatId)
            ?: groupRepository.save(Group(name = groupName, chatId = chatId))

        usersInfo.forEach { userInfo ->
            val user = userService.findUser(userInfo.id ?: "", userInfo.login ?: "")
                ?: TelegramUser(userId = userInfo.id, name = userInfo.name, login = userInfo.login)
                    .let { userService.save(it) }
            group.users.add(user)
        }

        groupRepository.save(group)
    }

    companion object {
        private val log by Logger()
    }
}