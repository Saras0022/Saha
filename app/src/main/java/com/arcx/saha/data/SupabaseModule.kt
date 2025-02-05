package com.arcx.saha.data

import com.arcx.saha.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
            supabaseUrl = BuildConfig.SUPABASE_URL
        ) {
            install(Postgrest)
            install(Auth) {
                scheme = "app"
                host = "supabase.com"
            }
            install(Realtime)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseDatabase(client: SupabaseClient): Postgrest {
        return client.postgrest
    }

    @Provides
    @Singleton
    fun provideSupabaseAuth(client: SupabaseClient): Auth {
        return client.auth
    }

    @Provides
    @Singleton
    fun provideSupabaseStorage(client: SupabaseClient): Storage {
        return client.storage
    }

    @Provides
    @Singleton
    fun provideSupabaseRealtime(client: SupabaseClient): Realtime {
        return client.realtime
    }

    @Provides
    fun provideMessages(client: SupabaseClient): MessageRepository {
        return MessageRepositoryImpl(client.auth, client.postgrest, client.storage)
    }

    @Provides
    fun provideAuthentication(client: SupabaseClient): AuthenticationRepository {
        return AuthenticationRepositoryImpl(client.auth)
    }

    @Provides
    fun provideUser(client: SupabaseClient): UserRepository {
        return UserRepositoryImpl(client.auth, client.postgrest)
    }
}