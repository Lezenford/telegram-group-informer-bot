package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.extensions.MENTION
import com.lezenford.groupnotifytelegrambot.extensions.START_MENTION_SYMBOL
import com.lezenford.groupnotifytelegrambot.extensions.TEXT_MENTION
import com.lezenford.groupnotifytelegrambot.extensions.removeMention
import org.telegram.telegrambots.meta.api.objects.Message

abstract class UserGroupCommand : BotCommand() {
    protected val Message.group: String?
        get() = text.split(" ").getOrNull(1)?.let {
            if (it.startsWith(START_MENTION_SYMBOL)) {
                it
            } else {
                "$START_MENTION_SYMBOL$it"
            }
        }

    protected val Message.usersInfo: Set<UserInfo>
        get() = entities.filterNot {
            it.type == MENTION && it.text != null && it.text == group
        }.mapNotNull {
            when (it.type) {
                MENTION -> UserInfo(login = it.text)
                TEXT_MENTION -> UserInfo(
                    id = it.user.id.toString(),
                    name = it.user.firstName,
                    login = it.user.userName
                )
                else -> null
            }
        }.toSet()

    protected val Message.usersTextLoginInfo: Set<UserInfo>
        get() = text.split(" ")
            .filter { it.startsWith(START_MENTION_SYMBOL) }
            .map { UserInfo(login = it) }.toSet()

    protected class UserInfo(
        val id: String? = null,
        val name: String? = null,
        login: String? = null
    ) {
        val login: String? = login?.removeMention()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UserInfo

            if (id != other.id) return false
            if (name != other.name) return false
            if (login != other.login) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id?.hashCode() ?: 0
            result = 31 * result + (name?.hashCode() ?: 0)
            result = 31 * result + (login?.hashCode() ?: 0)
            return result
        }
    }
}