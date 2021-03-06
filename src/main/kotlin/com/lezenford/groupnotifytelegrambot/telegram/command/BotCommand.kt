package com.lezenford.groupnotifytelegrambot.telegram.command

import com.fasterxml.jackson.annotation.JsonIgnore
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

abstract class BotCommand {
    @JsonIgnore
    open val publish: Boolean = false

    abstract val command: String

    open val description: String = ""

    abstract suspend fun execute(message: Message): BotApiMethod<*>?

    companion object {
        const val COMMAND_INIT_CHARACTER = "/"
    }
}