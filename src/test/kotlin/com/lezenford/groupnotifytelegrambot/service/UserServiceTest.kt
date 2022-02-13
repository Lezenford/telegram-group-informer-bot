package com.lezenford.groupnotifytelegrambot.service

import com.lezenford.groupnotifytelegrambot.BaseTest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UserServiceTest : BaseTest() {

    @Test
    fun `success create user with userId`() {
        runBlocking {
            val telegramUser = createUser().withId().withName()
            userService.save(telegramUser)
            val resultUser = userService.findUser(telegramUser.userId, telegramUser.login)!!
            assertThat(resultUser.userId).isEqualTo(telegramUser.userId)
            assertThat(resultUser.name).isEqualTo(telegramUser.name)
            assertThat(resultUser.login).isEqualTo(telegramUser.login)
        }
    }

    @Test
    fun `success create user with login`() {
        runBlocking {
            val telegramUser = createUser().withLogin()
            userService.save(telegramUser)
            val resultUser = userService.findUser(telegramUser.userId, telegramUser.login)!!
            assertThat(resultUser.userId).isEqualTo(telegramUser.userId)
            assertThat(resultUser.name).isEqualTo(telegramUser.name)
            assertThat(resultUser.login).isEqualTo(telegramUser.login)
        }
    }

    @Test
    fun `error create user without login and userId`() {
        runBlocking {
            val telegramUser = createUser().withName()
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { assertThat(userService.save(telegramUser)) }
        }
    }

    @Test
    fun `error create user without login and name`() {
        runBlocking {
            val telegramUser = createUser().withId()
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { assertThat(userService.save(telegramUser)) }
        }
    }

    @Test
    fun `find user by userId`() {
        runBlocking {
            val telegramUser = createUser().withId().withName()
            assertThat(userService.findUser(telegramUser.userId, telegramUser.login)).isNull()
            userService.save(telegramUser)
            val findUser = userService.findUser(telegramUser.userId, telegramUser.login)!!
            assertThat(findUser.userId).isEqualTo(telegramUser.userId)
        }
    }

    @Test
    fun `find user by login`() {
        runBlocking {
            val telegramUser = createUser().withName().withLogin()
            assertThat(userService.findUser(telegramUser.userId, telegramUser.login)).isNull()
            userService.save(telegramUser)
            val findUser = userService.findUser(telegramUser.userId, telegramUser.login)!!
            assertThat(findUser.login).isEqualTo(telegramUser.login)
        }
    }

    @Test
    fun `find all users by chatId and groupName`() {
        runBlocking {
            val telegramUser1 = createUser().withName().withLogin()
            val telegramUser2 = createUser().withName().withLogin()
            userService.save(telegramUser1)
            userService.save(telegramUser2)

            val group1 = createGroup().also {
                it.users.addAll(listOf(telegramUser1, telegramUser2))
            }
            val group2 = createGroup().also {
                it.users.add(telegramUser1)
            }

            var users = userService.findAllUsersByChatIdAndGroupName(chatId = group1.chatId, groupName = group1.name)
            assertThat(users).isEmpty()
            users = userService.findAllUsersByChatIdAndGroupName(chatId = group2.chatId, groupName = group2.name)
            assertThat(users).isEmpty()

            groupRepository.saveAll(listOf(group1, group2))

            users = userService.findAllUsersByChatIdAndGroupName(chatId = group1.chatId, groupName = group1.name)
            assertThat(users).hasSize(2)
            assertThat(users).contains(telegramUser1)
            assertThat(users).contains(telegramUser2)

            users = userService.findAllUsersByChatIdAndGroupName(chatId = group2.chatId, groupName = group2.name)
            assertThat(users).hasSize(1)
            assertThat(users).contains(telegramUser1)
        }
    }
}