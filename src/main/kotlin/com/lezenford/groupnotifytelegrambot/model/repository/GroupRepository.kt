package com.lezenford.groupnotifytelegrambot.model.repository

import com.lezenford.groupnotifytelegrambot.model.entity.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GroupRepository : JpaRepository<Group, Int> {

    fun findByNameAndChatId(name: String, chatId: String): Group?

    @Query(
        """
            SELECT G FROM Group G
            WHERE G.users.size > 0 AND G.chatId = :chatId
        """
    )
    fun findAllByChatId(chatId: String): List<Group>
}