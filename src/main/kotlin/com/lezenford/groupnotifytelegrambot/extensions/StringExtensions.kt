package com.lezenford.groupnotifytelegrambot.extensions

fun String.removeMention(): String = this.removePrefix(START_MENTION_SYMBOL)

fun String.escape(): String = this.map { if (it.code in 1..125) "\\$it" else it }.joinToString("")