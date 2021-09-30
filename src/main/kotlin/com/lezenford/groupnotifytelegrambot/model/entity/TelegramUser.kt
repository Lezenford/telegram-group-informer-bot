package com.lezenford.groupnotifytelegrambot.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "TELEGRAM_USER")
data class TelegramUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    val id: Int = 0,

    @Column(name = "USER_ID")
    var userId: String?,

    @Column(name = "NAME")
    var name: String?,

    @Column(name = "LOGIN")
    var login: String?
)