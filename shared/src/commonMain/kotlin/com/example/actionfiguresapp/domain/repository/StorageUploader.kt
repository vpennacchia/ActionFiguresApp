package com.example.actionfiguresapp.domain.repository

interface StorageUploader {
    suspend fun uploadImageBytes(path: String, bytes: ByteArray): String
}
