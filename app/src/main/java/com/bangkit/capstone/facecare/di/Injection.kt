package com.bangkit.capstone.facecare.di

import android.content.Context
import com.bangkit.capstone.facecare.data.UserRepository
import com.bangkit.capstone.facecare.data.pref.UserPreference
import com.bangkit.capstone.facecare.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}