package app.muko.mypantry.di

import android.content.Context
import androidx.room.Room
import app.muko.mypantry.data.dao.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalDatabaseModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(context: Context): LocalDatabase {
        return Room.databaseBuilder(context, LocalDatabase::class.java, "app.sqlite3")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }

    @Singleton
    @Provides
    fun boxDao(localDatabase: LocalDatabase): BoxDao {
        return localDatabase.boxDao()
    }

    @Singleton
    @Provides
    fun foodDao(localDatabase: LocalDatabase): FoodDao {
        return localDatabase.foodDao()
    }

    @Singleton
    @Provides
    fun unitDao(localDatabase: LocalDatabase): UnitDao {
        return localDatabase.unitDao()
    }

    @Singleton
    @Provides
    fun shopPlanDao(localDatabase: LocalDatabase): ShopPlanDao {
        return localDatabase.shopPlanDao()
    }
}
