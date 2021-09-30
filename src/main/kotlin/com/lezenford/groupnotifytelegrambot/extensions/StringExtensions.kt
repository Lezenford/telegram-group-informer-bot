package com.lezenford.groupnotifytelegrambot.extensions

fun String.removeMention(): String = this.removePrefix(START_MENTION_SYMBOL)