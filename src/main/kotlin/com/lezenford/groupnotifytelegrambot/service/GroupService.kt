package com.lezenford.groupnotifytelegrambot.service

import com.lezenford.groupnotifytelegrambot.configuration.CacheConfiguration
import com.lezenford.groupnotifytelegrambot.model.entity.Group
import com.lezenford.groupnotifytelegrambot.model.repository.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupService(
    private val groupRepository: GroupRepository
) {
    @Transactional
    @CacheEvict(CacheConfiguration.TELEGRAM_USER_CACHE, allEntries = true)
    fun save(group: Group): Group = groupRepository.save(group)

    @Transactional
    @CacheEvict(CacheConfiguration.TELEGRAM_USER_CACHE, allEntries = true)
    fun saveAll(groups: Iterable<Group>): List<Group> =
        groupRepository.saveAll(groups)

    suspend fun findAllByChatId(chatId: String): List<Group> =
        withContext(Dispatchers.IO) { groupRepository.findAllByChatId(chatId) }

    suspend fun findByNameAndChatId(name: String, chatId: String): Group? =
        withContext(Dispatchers.IO) { groupRepository.findByNameAndChatId(name, chatId) }
}