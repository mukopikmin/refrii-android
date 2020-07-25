package app.muko.mypantry.di

import android.content.Context
import androidx.room.Room
import app.muko.mypantry.data.dao.LocalDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TestLocalDatabaseModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(context: Context): LocalDatabase {
        return Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}
