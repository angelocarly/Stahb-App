package be.magnias.stahb.injection.module

import android.app.Application
import android.content.Context
import be.magnias.stahb.persistence.dao.TabDao
import be.magnias.stahb.persistence.*
import be.magnias.stahb.service.TabService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module which provides all required dependencies about databases
 */
@Module
class DatabaseModule(private val application: Application) {


    @Provides
    @Singleton
    internal fun provideTabRepository(tabDao: TabDao): TabService {
        return TabService(tabDao)
    }

    @Provides
    @Singleton
    internal fun provideTabDao(tabDatabase: TabDatabase): TabDao {
        return tabDatabase.tabDao()
    }

    @Provides
    @Singleton
    internal fun provideTabDatabase(context: Context): TabDatabase {
        return TabDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    internal fun provideUserRepository(): UserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return application
    }
}