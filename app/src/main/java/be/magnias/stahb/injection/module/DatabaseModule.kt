package be.magnias.stahb.injection.module

import android.app.Application
import android.content.Context
import be.magnias.stahb.persistence.dao.TabDao
import be.magnias.stahb.persistence.*
import be.magnias.stahb.service.ITabService
import be.magnias.stahb.service.IUserService
import be.magnias.stahb.service.TabService
import be.magnias.stahb.service.UserService
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
    internal fun provideTabService(): ITabService {
        return TabService()
    }

    @Provides
    @Singleton
    internal fun provideTabRepository(tabDao: TabDao): ITabRepository {
        return TabRepository(tabDao)
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
    internal fun provideUserRepository(): IUserRepository {
        return UserRepository()
    }

    @Provides
    @Singleton
    internal fun provideUserService(): IUserService {
        return UserService()
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return application
    }
}