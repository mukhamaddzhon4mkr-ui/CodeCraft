package com.example.codecraft.data.model
data class Lesson (
    val id: String,
    val language: String,
    val title: String,
    val description: String,
    val theoryText: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val rewardPoints: Int = 10
)