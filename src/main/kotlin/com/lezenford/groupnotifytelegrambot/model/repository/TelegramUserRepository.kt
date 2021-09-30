package com.lezenford.groupnotifytelegrambot.model.repository

import com.lezenford.groupnotifytelegrambot.model.entity.TelegramUser
import org.springframework.data.jpa.repository.JpaRepository

interface TelegramUserRepository : JpaRepository<TelegramUser, Int> {

    fun findFirstByUserIdOrLogin(userId: String, login: String): TelegramUser?
}