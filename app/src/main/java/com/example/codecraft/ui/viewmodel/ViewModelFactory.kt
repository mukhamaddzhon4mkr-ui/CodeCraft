package com.example.codecraft.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.codecraft.data.repository.ProgressRepository
import com.example.codecraft.data.repository.UserRepository
import com.example.codecraft.ui.theme.home.viewmodel.HomeViewModel
import com.example.codecraft.ui.theme.lessons.viewmodel.LessonsViewModel
import com.example.codecraft.ui.theme.lessons.viewmodel.LessonDetailViewModel
import com.example.codecraft.ui.theme.profile.viewmodel.ProfileViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val progressRepository: ProgressRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userRepository, progressRepository) as T
            }
            modelClass.isAssignableFrom(LessonsViewModel::class.java) -> {
                LessonsViewModel(progressRepository) as T
            }
            modelClass.isAssignableFrom(LessonDetailViewModel::class.java) -> {
                LessonDetailViewModel(progressRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository, progressRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
