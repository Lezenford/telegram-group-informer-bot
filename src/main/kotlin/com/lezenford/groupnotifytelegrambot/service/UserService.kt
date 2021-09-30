package com.lezenford.groupnotifytelegrambot.service

import com.lezenford.groupnotifytelegrambot.configuration.CacheConfiguration
import com.lezenford.groupnotifytelegrambot.extensions.Logger
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
    private val groupRepository: GroupRepository
) {

    @Cacheable(value = [CacheConfiguration.TELEGRAM_USER_CACHE], unless = "#result == null || #result.isEmpty()")
    suspend fun findAllUsersByChatIdAndGroupName(chatId: String, groupName: String): List<TelegramUser> =
        groupRepository.findByNameAndChatId(groupName, chatId)?.users?.toList() ?: emptyList()

    @Cacheable(value = [CacheConfiguration.TELEGRAM_USER_CACHE], unless = "#result == null")
    suspend fun findUser(userId: String, userLogin: String?): TelegramUser? {
        return userRepository.findFirstByUserIdOrLogin(userId, userLogin ?: "")
    }

    @Transactional
    @CacheEvict(value = [CacheConfiguration.TELEGRAM_USER_CACHE], allEntries = true)
    suspend fun save(user: TelegramUser): TelegramUser = userRepository.save(user)

    companion object {
        private val log by Logger()
    }
}