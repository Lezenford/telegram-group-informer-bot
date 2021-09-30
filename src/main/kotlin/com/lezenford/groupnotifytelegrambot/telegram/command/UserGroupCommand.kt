package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.extensions.MENTION
import com.lezenford.groupnotifytelegrambot.extensions.START_MENTION_SYMBOL
import com.lezenford.groupnotifytelegrambot.extensions.TEXT_MENTION
import com.lezenford.groupnotifytelegrambot.extensions.removeMention
import org.telegram.telegrambots.meta.api.objects.Message

abstract class UserGroupCommand : BotCommand() {
    protected fun getGroup(message: Message): String? {
        return message.text.split(" ").getOrNull(1)?.let {
            if (it.startsWith(START_MENTION_SYMBOL)) {
                it
            } else {
                "$START_MENTION_SYMBOL$it"
            }
        }
    }

    protected fun getUsersInfo(message: Message): List<UserInfo> = message.entities.mapNotNull {
        when (it.type) {
            MENTION -> UserInfo(login = it.text)
            TEXT_MENTION -> UserInfo(
                id = it.user.id.toString(),
                name = it.user.firstName,
                login = it.user.userName
            )
            else -> null
        }
    }

    protected class UserInfo(
        val id: String? = null,
        val name: String? = null,
        login: String? = null
    ) {
        val login: String? = login?.removeMention()
    }
}