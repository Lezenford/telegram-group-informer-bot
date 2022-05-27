package com.lezenford.groupnotifytelegrambot.service

import com.lezenford.groupnotifytelegrambot.configuration.CacheConfiguration
import com.lezenford.groupnotifytelegrambot.model.entity.TelegramUser
import com.lezenford.groupnotifytelegrambot.model.repository.GroupRepository
import com.lezenford.groupnotifytelegrambot.model.repository.TelegramUserRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: TelegramUserRepository,
    private val groupRepository: GroupRepository,
) {

    @Cacheable(value = [CacheConfiguration.TELEGRAM_USER_CACHE], unless = "#result.isEmpty()")
    fun findAllUsersByChatIdAndGroupName(chatId: String, groupName: String): List<TelegramUser> =
        groupRepository.findByNameAndChatId(groupName, chatId)?.users?.toList() ?: emptyList()

    @Cacheable(value = [CacheConfiguration.TELEGRAM_USER_CACHE], unless = "#result == null")
    fun findUser(userId: String?, userLogin: String?): TelegramUser? =
        userRepository.findFirstByUserIdOrLogin(userId ?: "", userLogin ?: "")

    @Transactional
    @CacheEvict(value = [CacheConfiguration.TELEGRAM_USER_CACHE], allEntries = true)
    fun save(user: TelegramUser): TelegramUser {
        user.apply {
            login ?: userId?.run { name }
            ?: throw IllegalArgumentException("User must have userId with name or login")
        }
        return userRepository.save(user)
    }
}