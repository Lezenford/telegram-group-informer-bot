package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.BaseTest
import com.lezenford.groupnotifytelegrambot.extensions.MENTION
import com.lezenford.groupnotifytelegrambot.extensions.TEXT_MENTION
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class AddCommandTest : BaseTest() {

    @Autowired
    private lateinit var addCommand: AddCommand

    @Test
    fun `successfully add user to existing group with text mention`() {
        var group = createGroup()
        groupRepository.save(group)
        val user = createUser().withId().withLogin().withName()

        val message = simpleMessage.apply {
            text = "/${addCommand.command} ${group.name} @${user.login}"
        }.addEntity(TEXT_MENTION, user = user)

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).noneMatch { it.userId == user.userId }

        runBlocking { addCommand.execute(message) }

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).anyMatch { it.userId == user.userId }
    }

    @Test
    fun `successfully add user to new group with text mention`() {
        var group = createGroup()
        val user = createUser().withId().withLogin().withName()

        val message = simpleMessage.apply {
            text = "/${addCommand.command} ${group.name} @${user.login}"
        }.addEntity(TEXT_MENTION, user = user)

        Assertions.assertThat(groupRepository.findByNameAndChatId(group.name, defaultChatId.toString())).isNull()

        Assertions.assertThat(group.users).noneMatch { it.userId == user.userId }

        runBlocking { addCommand.execute(message) }

        group = groupRepository.findByNameAndChatId(group.name, defaultChatId.toString())!!

        Assertions.assertThat(group.users).anyMatch { it.userId == user.userId }
    }

    @Test
    fun `successfully add user to existing group with mention`() {
        var group = createGroup()
        groupRepository.save(group)
        val user = createUser().withId().withLogin().withName()

        val message = simpleMessage.apply {
            text = "/${addCommand.command} ${group.name} @${user.login}"
        }.addEntity(MENTION, user.login!!)

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).noneMatch { it.login == user.login }

        runBlocking { addCommand.execute(message) }

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).anyMatch { it.login == user.login }
    }

    @Test
    fun `successfully add user to new group with mention`() {
        var group = createGroup()
        val user = createUser().withId().withLogin().withName()

        val message = simpleMessage.apply {
            text = "/${addCommand.command} ${group.name} @${user.login}"
        }.addEntity(MENTION, user.login!!)

        Assertions.assertThat(groupRepository.findByNameAndChatId(group.name, defaultChatId.toString())).isNull()

        Assertions.assertThat(group.users).noneMatch { it.login == user.login }

        runBlocking { addCommand.execute(message) }

        group = groupRepository.findByNameAndChatId(group.name, defaultChatId.toString())!!

        Assertions.assertThat(group.users).anyMatch { it.login == user.login }
    }

    @Test
    fun `skip user without entity in message`() {
        var group = createGroup()
        val user = createUser().withId().withLogin().withName()

        val message = simpleMessage.apply {
            text = "/${addCommand.command} ${group.name} @${user.login}"
        }

        Assertions.assertThat(groupRepository.findByNameAndChatId(group.name, defaultChatId.toString())).isNull()

        Assertions.assertThat(group.users).noneMatch { it.login == user.login }

        runBlocking { addCommand.execute(message) }

        group = groupRepository.findByNameAndChatId(group.name, defaultChatId.toString())!!

        Assertions.assertThat(group.users).noneMatch { it.login == user.login }
    }
}