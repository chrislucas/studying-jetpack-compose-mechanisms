package com.br.configflow

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(@ApplicationContext context: Context): FirebaseRemoteConfig? {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                null
            } else {
                FirebaseRemoteConfig.getInstance()
            }
        } catch (e: Exception) {
            null
        }
    }

    @Provides
    @Singleton
    fun provideConfigFlow(impl: ConfigFlowImpl): ConfigFlow {
        return impl
    }
}
