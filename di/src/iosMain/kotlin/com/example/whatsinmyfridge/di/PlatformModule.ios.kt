package com.example.whatsinmyfridge.di

import com.example.whatsinmyfridge.infrastructure.local.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory() }
}
