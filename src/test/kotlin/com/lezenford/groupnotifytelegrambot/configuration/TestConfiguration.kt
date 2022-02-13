package com.lezenford.groupnotifytelegrambot.configuration

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class TestConfiguration {

    @Bean
    fun exchangeFunction(): ExchangeFunction = Mockito.mock(ExchangeFunction::class.java)

    @Bean
    @Primary
    fun mockWebClient(): WebClient = WebClient.builder().exchangeFunction(exchangeFunction()).build().also {
        Mockito.`when`(exchangeFunction().exchange(any(ClientRequest::class.java)))
            .thenReturn(
                Mono.just(
                    ClientResponse
                        .create(HttpStatus.OK)
                        .body("{\"ok\":true,\"result\":true}") //Need intercept the initialization method called
                        .build()
                )
            )
    }
}