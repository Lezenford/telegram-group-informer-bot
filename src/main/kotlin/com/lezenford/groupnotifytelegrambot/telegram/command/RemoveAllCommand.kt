package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.configuration.CacheConfiguration
import com.lezenford.groupnotifytelegrambot.model.repository.GroupRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import javax.transaction.Transactional

@Component
class RemoveAllCommand(
    private val groupRepository: GroupRepository
) : UserGroupCommand() {
    override val command: String = "removeall"
    override val description: String = "Удалить пользователя из всех групп уведомлений"
    override val publish: Boolean = true

    @Transactional
    @CacheEvict(CacheConfiguration.TELEGRAM_USER_CACHE, allEntries = true)
    override suspend fun execute(message: Message): BotApiMethod<*>? {
        val usersInfo = message.usersInfo
        groupRepository.findAllByChatId(message.chatId.toString()).onEach { group ->
            usersInfo.forEach { userInfo ->
                group.users.removeIf { it.userId == (userInfo.id ?: "") || it.login == (userInfo.login ?: "") }
            }
        }.also { groupRepository.saveAll(it) }
        return SendMessage(message.chatId.toString(), "Команда выполнена успешно").apply { disableNotification = true }
    }
}