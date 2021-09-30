package com.lezenford.groupnotifytelegrambot.model.entity

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "TELEGRAM_GROUP")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    val id: Int = 0,

    @Column(name = "NAME")
    val name: String,

    @Column(name = "CHAT_ID")
    val chatId: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
        name = "TELEGRAM_GROUP_USER",
        joinColumns = [JoinColumn(name = "GROUP_ID")],
        inverseJoinColumns = [JoinColumn(name = "USER_ID")]
    )
    val users: MutableSet<TelegramUser> = mutableSetOf()
)