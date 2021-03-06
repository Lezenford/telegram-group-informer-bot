package com.lezenford.groupnotifytelegrambot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "telegram")
data class TelegramProperties(
    val username: String,
    val token: String,
    val path: String,
)