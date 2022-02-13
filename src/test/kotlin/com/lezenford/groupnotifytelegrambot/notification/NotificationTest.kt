package com.lezenford.groupnotifytelegrambot.notification

import com.lezenford.groupnotifytelegrambot.BaseTest
import com.lezenford.groupnotifytelegrambot.controller.TelegramController
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class NotificationTest : BaseTest() {

    @Autowired
    private lateinit var telegramController: TelegramController

    @Test
    fun `send notification for group by simple message`() {
        runBlocking {
            val telegramUserWithLogin = createUser().withLogin().withName()
            val telegramUserWithId = createUser().withId().withName()
            userService.save(telegramUserWithLogin)
            userService.save(telegramUserWithId)

            val group = createGroup().also {
                it.users.addAll(listOf(telegramUserWithLogin, telegramUserWithId))
            }
            groupRepository.save(group)

            val update = Update().apply {
                message = simpleMessage.apply {
                    text = group.name
                }
            }
            val receive = telegramController.receive(update).body
            assertThat(receive is SendMessage).isTrue
            if (receive is SendMessage) {
                assertThat(receive.chatId).isEqualTo(group.chatId)
                assertThat(receive.parseMode).isEqualTo("MarkdownV2")
                assertThat(receive.replyToMessageId).isEqualTo(update.message.messageId)
                assertThat(receive.text.replace("\\", "")).contains(telegramUserWithLogin.login)
                assertThat(receive.text).contains("user?id=${telegramUserWithId.userId}")
            }
        }
    }

    @Test
    fun `send notification for group by image message from group member`() {
        runBlocking {
            val telegramUserWithLogin = createUser().withName().withLogin()
            val telegramUserWithId = createUser().withId().withName()
            userService.save(telegramUserWithLogin)
            userService.save(telegramUserWithId)

            val group = createGroup().also {
                it.users.addAll(listOf(telegramUserWithLogin, telegramUserWithId))
            }
            groupRepository.save(group)

            val update = Update().apply {
                message = simpleMessage.apply {
                    text = group.name
                    from.id = telegramUserWithId.userId!!.toLong()
                }
            }

            val receive = telegramController.receive(update).body
            assertThat(receive is SendMessage).isTrue
            if (receive is SendMessage) {
                assertThat(receive.chatId).isEqualTo(group.chatId)
                assertThat(receive.parseMode).isEqualTo("MarkdownV2")
                assertThat(receive.replyToMessageId).isEqualTo(update.message.messageId)
                assertThat(receive.text.replace("\\", "")).contains(telegramUserWithLogin.login)
                assertThat(receive.text).doesNotContain("user?id=${telegramUserWithId.userId}")
            }
        }

    }

    @Test
    fun `send notification for group by image message`() {
        runBlocking {
            val telegramUserWithLogin = createUser().withName().withLogin()
            val telegramUserWithId = createUser().withId().withName()
            userService.save(telegramUserWithLogin)
            userService.save(telegramUserWithId)

            val group = createGroup().also {
                it.users.addAll(listOf(telegramUserWithLogin, telegramUserWithId))
            }
            groupRepository.save(group)

            val update = Update().apply {
                message = simpleMessage.apply {
                    caption = group.name
                }
            }
            val receive = telegramController.receive(update).body
            assertThat(receive is SendMessage).isTrue
            if (receive is SendMessage) {
                assertThat(receive.chatId).isEqualTo(group.chatId)
                assertThat(receive.parseMode).isEqualTo("MarkdownV2")
                assertThat(receive.replyToMessageId).isEqualTo(update.message.messageId)
                assertThat(receive.text.replace("\\", "")).contains(telegramUserWithLogin.login)
                assertThat(receive.text).contains("user?id=${telegramUserWithId.userId}")
            }
        }
    }

    @Test
    fun `send notification for group by simple message from group member`() {
        runBlocking {
            val telegramUserWithLogin = createUser().withName().withLogin()
            val telegramUserWithId = createUser().withId().withName()
            userService.save(telegramUserWithLogin)
            userService.save(telegramUserWithId)

            val group = createGroup().also {
                it.users.addAll(listOf(telegramUserWithLogin, telegramUserWithId))
            }
            groupRepository.save(group)

            val update = Update().apply {
                message = simpleMessage.apply {
                    caption = group.name
                    from.id = telegramUserWithId.userId!!.toLong()
                }
            }
            val receive = telegramController.receive(update).body
            assertThat(receive is SendMessage).isTrue
            if (receive is SendMessage) {
                assertThat(receive.chatId).isEqualTo(group.chatId)
                assertThat(receive.parseMode).isEqualTo("MarkdownV2")
                assertThat(receive.replyToMessageId).isEqualTo(update.message.messageId)
                assertThat(receive.text.replace("\\", "")).contains(telegramUserWithLogin.login)
                assertThat(receive.text).doesNotContain("user?id=${telegramUserWithId.userId}")
            }
        }
    }

    @Test
    fun `send notification for group with single member by simple message from group member`() {
        runBlocking {
            val telegramUserWithId = createUser().withId().withName().withLogin()
            userService.save(telegramUserWithId)

            val group = createGroup().also {
                it.users.add(telegramUserWithId)
            }
            groupRepository.save(group)

            val update = Update().apply {
                message = simpleMessage.apply {
                    text = group.name
                    from.id = telegramUserWithId.userId!!.toLong()
                }
            }
            val receive = telegramController.receive(update).body
            assertThat(receive).isNull()
        }
    }
}