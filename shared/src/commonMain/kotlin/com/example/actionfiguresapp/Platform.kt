package com.example.actionfiguresapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform