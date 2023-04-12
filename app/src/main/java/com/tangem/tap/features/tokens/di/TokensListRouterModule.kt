package com.tangem.tap.features.tokens.di

import com.tangem.tap.features.tokens.presentation.router.DefaultTokensListRouter
import com.tangem.tap.features.tokens.presentation.router.TokensListRouter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * @author Andrew Khokhlov on 24/03/2023
 */
@Module
@InstallIn(ViewModelComponent::class)
internal object TokensListRouterModule {

    @Provides
    @ViewModelScoped
    fun provideTokensListRouter(): TokensListRouter = DefaultTokensListRouter()
}