package js.apps.accesscontrol.di


import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealtimeModule {

    @Provides
    @Singleton
    fun provideDb():FirebaseDatabase{
        return Firebase.database
    }
    @Singleton
    @Provides
    fun provideRealtimeDbUsers(
        db:FirebaseDatabase
    ):DatabaseReference{
        return db
            .getReference("alumnos")
    }



    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class UsersReference

}