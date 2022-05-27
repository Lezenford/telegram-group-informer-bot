package com.lezenford.groupnotifytelegrambot.telegram.command

import com.lezenford.groupnotifytelegrambot.BaseTest
import com.lezenford.groupnotifytelegrambot.extensions.MENTION
import com.lezenford.groupnotifytelegrambot.extensions.TEXT_MENTION
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class RemoveAllCommandTest : BaseTest() {
    @Autowired
    private lateinit var removeAllCommand: RemoveAllCommand

    @Test
    fun `remove user with mention entity from all groups`() {
        var group1 = createGroup()
        groupRepository.save(group1)
        var group2 = createGroup()
        groupRepository.save(group2)

        val user = createUser().withId().withLogin().withName()
        userService.save(user)

        group1.users.add(user)
        groupRepository.save(group1)
        group2.users.add(user)
        groupRepository.save(group2)

        group1 = groupRepository.findById(group1.id).get()
        group2 = groupRepository.findById(group2.id).get()

        Assertions.assertThat(group1.users).anyMatch { it.login == user.login }
        Assertions.assertThat(group2.users).anyMatch { it.login == user.login }

        val message = simpleMessage.apply {
            text = "/${removeAllCommand.command} ${group1.name} @${user.login}"
        }.addEntity(MENTION, user.login!!)

        runBlocking { removeAllCommand.execute(message) }

        group1 = groupRepository.findById(group1.id).get()
        group2 = groupRepository.findById(group2.id).get()

        Assertions.assertThat(group1.users).noneMatch { it.login == user.login }
        Assertions.assertThat(group2.users).noneMatch { it.login == user.login }
    }

    @Test
    fun `remove user with text mention entity from all groups`() {
        var group1 = createGroup()
        groupRepository.save(group1)
        var group2 = createGroup()
        groupRepository.save(group2)

        val user = createUser().withId().withLogin().withName()
        userService.save(user)

        group1.users.add(user)
        groupRepository.save(group1)
        group2.users.add(user)
        groupRepository.save(group2)

        group1 = groupRepository.findById(group1.id).get()
        group2 = groupRepository.findById(group2.id).get()

        Assertions.assertThat(group1.users).anyMatch { it.login == user.login }
        Assertions.assertThat(group2.users).anyMatch { it.login == user.login }

        val message = simpleMessage.apply {
            text = "/${removeAllCommand.command} ${group1.name} @${user.name}"
        }.addEntity(TEXT_MENTION, user = user)

        runBlocking { removeAllCommand.execute(message) }

        group1 = groupRepository.findById(group1.id).get()
        group2 = groupRepository.findById(group2.id).get()

        Assertions.assertThat(group1.users).noneMatch { it.login == user.login }
        Assertions.assertThat(group2.users).noneMatch { it.login == user.login }
    }

    @Test
    fun `remove user without entity from all groups`() {
        var group1 = createGroup()
        groupRepository.save(group1)
        var group2 = createGroup()
        groupRepository.save(group2)

        val user = createUser().withId().withLogin().withName()
        userService.save(user)

        group1.users.add(user)
        groupRepository.save(group1)
        group2.users.add(user)
        groupRepository.save(group2)

        group1 = groupRepository.findById(group1.id).get()
        group2 = groupRepository.findById(group2.id).get()

        Assertions.assertThat(group1.users).anyMatch { it.login == user.login }
        Assertions.assertThat(group2.users).anyMatch { it.login == user.login }

        val message = simpleMessage.apply {
            text = "/${removeAllCommand.command} ${group1.name} @${user.login}"
        }

        runBlocking { removeAllCommand.execute(message) }

        group1 = groupRepository.findById(group1.id).get()
        group2 = groupRepository.findById(group2.id).get()

        Assertions.assertThat(group1.users).noneMatch { it.login == user.login }
        Assertions.assertThat(group2.users).noneMatch { it.login == user.login }
    }
}