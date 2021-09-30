package com.lezenford.groupnotifytelegrambot.model.repository

import com.lezenford.groupnotifytelegrambot.model.entity.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Int> {

    fun findByNameAndChatId(name: String, chatId: String): Group?
}