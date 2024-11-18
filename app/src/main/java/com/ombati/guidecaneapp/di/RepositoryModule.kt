package com.ombati.guidecaneapp.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepository
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGuideCaneRepository(
        firebaseFirestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): GuideCaneRepository {
        return GuideCaneRepositoryImpl(
            firebaseDB = firebaseFirestore,
            ioDispatcher = ioDispatcher,
        )
    }
}
