package com.example.memorygame.models

import org.intellij.lang.annotations.Identifier

data class MemoryCard(
    val identifier: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)