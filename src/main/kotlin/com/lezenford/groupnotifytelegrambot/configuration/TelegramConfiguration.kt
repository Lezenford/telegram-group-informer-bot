package com.lezenford.groupnotifytelegrambot.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class TelegramConfiguration {

    @Bean
    fun webClient(): WebClient = WebClient.create()
}