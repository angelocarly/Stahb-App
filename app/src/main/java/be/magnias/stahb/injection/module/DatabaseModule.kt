package be.magnias.stahb.injection.module

import android.app.Application
import android.content.Context
import be.magnias.stahb.model.TabDao
import be.magnias.stahb.model.TabInfoDao
import be.magnias.stahb.persistence.TabDatabase
import be.magnias.stahb.persistence.TabInfoDatabase
import be.magnias.stahb.persistence.TabInfoRepository
import be.magnias.stahb.persistence.TabRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module which provides all required dependencies about network
 */
@Module
class DatabaseModule(private val application: Application) {

    //TABINFO
    @Provides
    @Singleton
    internal fun provideTabInfoRepository(tabInfoDao: TabInfoDao): TabInfoRepository {
        return TabInfoRepository(tabInfoDao)
    }

    @Provides
    @Singleton
    internal fun provideTabInfoDao(tabInfoDatabase: TabInfoDatabase): TabInfoDao {
        return tabInfoDatabase.tabInfoDao()
    }

    @Provides
    @Singleton
    internal fun provideTabInfoDatabase(context: Context): TabInfoDatabase {
        return TabInfoDatabase.getInstance(context)
    }

    //TAB
    @Provides
    @Singleton
    internal fun provideTabRepository(tabDao: TabDao): TabRepository {
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
    fun provideApplicationContext(): Context {
        return application
    }
}