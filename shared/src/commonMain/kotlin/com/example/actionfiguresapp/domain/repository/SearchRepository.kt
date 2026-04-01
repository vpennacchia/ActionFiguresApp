package com.example.actionfiguresapp.domain.repository

import com.example.actionfiguresapp.domain.model.ActionFigure

interface SearchRepository {
    suspend fun searchFigures(query: String): Result<List<ActionFigure>>
}
