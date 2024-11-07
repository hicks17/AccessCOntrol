package js.apps.accesscontrol.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import js.apps.accesscontrol.network.LoginRepoAuth
import js.apps.accesscontrol.network.LoginRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideLoginRepository(auth: FirebaseAuth): LoginRepository {
        return LoginRepoAuth(auth)
    }
}