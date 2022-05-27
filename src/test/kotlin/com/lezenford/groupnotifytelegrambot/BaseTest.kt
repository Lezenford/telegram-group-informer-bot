package com.lezenford.groupnotifytelegrambot

import com.lezenford.groupnotifytelegrambot.extensions.Logger
import com.lezenford.groupnotifytelegrambot.model.entity.Group
import com.lezenford.groupnotifytelegrambot.model.entity.TelegramUser
import com.lezenford.groupnotifytelegrambot.model.repository.GroupRepository
import com.lezenford.groupnotifytelegrambot.service.UserService
import com.lezenford.groupnotifytelegrambot.telegram.TelegramBot
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.MessageEntity
import org.telegram.telegrambots.meta.api.objects.User
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseTest {
    @Autowired
    protected lateinit var groupRepository: GroupRepository

    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var telegramBot: TelegramBot

    protected val simpleMessage: Message
        get() = Message().apply {
            messageId = counter.incrementAndGet()
            from = User().apply {
                id = defaultChatId
                firstName = "TestName"
            }
            date = Date().time.toInt()
            chat = Chat().apply {
                id = defaultChatId
            }
            entities = mutableListOf()
        }

    protected fun createUser(): TelegramUser = TelegramUser(userId = null, login = null, name = null)
    protected fun TelegramUser.withId() = copy(userId = counter.incrementAndGet().toString())
    protected fun TelegramUser.withLogin() = copy(login = "test_login_${UUID.randomUUID().toString().replace("-", "")}")
    protected fun TelegramUser.withName() = copy(name = "test_name_${UUID.randomUUID().toString().replace("-", "")}")
    protected fun Message.addEntity(type: String, text: String = "", user: TelegramUser? = null): Message {
        val messageEntity = MessageEntity().also {
            it.type = type
            it.offset = this.text.indexOf(text)
            it.length = text.length
            user?.apply {
                it.user = User().apply {
                    id = user.userId!!.toLong()
                    firstName = user.name!!
                    userName = user.login!!
                }
            }
        }
        entities.add(messageEntity)
        return this
    }

    protected fun createGroup(chatId: Long = defaultChatId): Group = Group(
        chatId = chatId.toString(),
        name = "@test_group_${UUID.randomUUID().toString().replace("-", "")}"
    )

    companion object {
        val counter = AtomicInteger()
        const val defaultChatId = 123L
        private val log by Logger()
    }
}
