package com.example.todolist.utils

fun Boolean.toDb(): Int = if (this) 1 else 0

fun Int.toBoolean(): Boolean = this == 1