package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.BaseTest
import com.lezenford.groupnotifytelegrambot.extensions.MENTION
import com.lezenford.groupnotifytelegrambot.extensions.TEXT_MENTION
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class RemoveCommandTest : BaseTest() {

    @Autowired
    private lateinit var removeCommand: RemoveCommand

    @Test
    fun `remove user with mention entity from group`() {
        var group = createGroup()
        groupRepository.save(group)

        val user = createUser().withId().withLogin().withName()
        userService.save(user)

        group.users.add(user)
        groupRepository.save(group)

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).anyMatch { it.login == user.login }

        val message = simpleMessage.apply {
            text = "/${removeCommand.command} ${group.name} @${user.login}"
        }.addEntity(MENTION, user.login!!)

        runBlocking { removeCommand.execute(message) }

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).noneMatch { it.login == user.login }
    }

    @Test
    fun `remove user with text mention entity from group`() {
        var group = createGroup()
        groupRepository.save(group)

        val user = createUser().withId().withLogin().withName()
        userService.save(user)

        group.users.add(user)
        groupRepository.save(group)

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).anyMatch { it.login == user.login }

        val message = simpleMessage.apply {
            text = "/${removeCommand.command} ${group.name} @${user.name}"
        }.addEntity(TEXT_MENTION, user = user)

        runBlocking { removeCommand.execute(message) }

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).noneMatch { it.login == user.login }
    }

    @Test
    fun `remove user without entity from group`() {
        var group = createGroup()
        groupRepository.save(group)

        val user = createUser().withId().withLogin().withName()
        userService.save(user)

        group.users.add(user)
        groupRepository.save(group)

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).anyMatch { it.login == user.login }

        val message = simpleMessage.apply {
            text = "/${removeCommand.command} ${group.name} @${user.login}"
        }

        runBlocking { removeCommand.execute(message) }

        group = groupRepository.findById(group.id).get()

        Assertions.assertThat(group.users).noneMatch { it.login == user.login }
    }
}