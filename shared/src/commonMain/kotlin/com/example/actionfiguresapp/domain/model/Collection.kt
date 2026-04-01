package com.example.actionfiguresapp.domain.model

data class Collection(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val figures: List<ActionFigure> = emptyList(),
    val createdAt: Long = 0L
) {
    val totalValue: Double
        get() = figures.sumOf { it.price ?: 0.0 }

    val figureCount: Int
        get() = figures.size
}
