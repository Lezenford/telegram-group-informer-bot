package com.lezenford.groupnotifytelegrambot.controller

import com.lezenford.groupnotifytelegrambot.extensions.Logger
import com.lezenford.groupnotifytelegrambot.telegram.TelegramBot
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@RestController
@RequestMapping("telegram")
class TelegramController(
    private val telegramBot: TelegramBot
) {

    @PostMapping(value = ["/\${telegram.token}"])
    suspend fun receive(@RequestBody update: Update): ResponseEntity<BotApiMethod<*>> {
        return kotlin.runCatching {
            telegramBot.receive(update)
        }.onFailure {
            log.error("Receive update error. Message: $update", it)
        }.getOrNull().let { ResponseEntity.ok(it) }
    }

    companion object {
        private val log by Logger()
    }
}