package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.configuration.CacheConfiguration
import com.lezenford.groupnotifytelegrambot.extensions.Logger
import com.lezenford.groupnotifytelegrambot.model.repository.GroupRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class RemoveCommand(
    private val groupRepository: GroupRepository
) : UserGroupCommand() {
    override val command: String = "remove"
    override val description: String = "Удалить пользователя из группы уведомлений"
    override val publish: Boolean = true

    @Transactional
    @CacheEvict(CacheConfiguration.TELEGRAM_USER_CACHE, allEntries = true)
    override suspend fun execute(message: Message): BotApiMethod<*>? {
        getGroup(message)?.also { groupName ->
            groupRepository.findByNameAndChatId(groupName, message.chatId.toString())?.also { group ->
                getUsersInfo(message).forEach { userInfo ->
                    group.users.removeIf {
                        it.userId == (userInfo.id ?: "") || it.login == (userInfo.login ?: "")
                    }
                }
                groupRepository.save(group)
            }
        }
        return SendMessage(message.chatId.toString(), "Команда выполнена успешно")
    }

    companion object {
        private val log by Logger()
    }
}