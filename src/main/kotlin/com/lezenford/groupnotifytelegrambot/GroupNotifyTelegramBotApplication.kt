package com.lezenford.groupnotifytelegrambot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

//@EnableWebFlux
@SpringBootApplication
//@EnableR2dbcRepositories
@ConfigurationPropertiesScan
class GroupNotifyTelegramBotApplication

fun main(args: Array<String>) {
    runApplication<GroupNotifyTelegramBotApplication>(*args)
}
