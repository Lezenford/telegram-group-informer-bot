package com.lezenford.groupnotifytelegrambot.telegram

import com.lezenford.groupnotifytelegrambot.extensions.Logger
import com.lezenford.groupnotifytelegrambot.extensions.START_MENTION_SYMBOL
import com.lezenford.groupnotifytelegrambot.telegram.command.BotCommand
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class CommandHandler(
    commands: Collection<BotCommand>
) {
    private val commands: Map<String, BotCommand> = commands.associateBy { it.command }

    suspend fun execute(update: Update): BotApiMethod<*>? {
        if (update.message?.isCommand == true) {
            val command = update.message.text.takeWhile { it != ' ' }.drop(1)
                .split(START_MENTION_SYMBOL).first().lowercase()
            return commands[command]?.execute(update.message)
        } else {
            throw IllegalArgumentException("Update $update doesn't contain command")
        }
    }

    fun publicCommands(): List<BotCommand> = commands.values.filter { it.publish }

    companion object {
        private val log by Logger()
    }
}