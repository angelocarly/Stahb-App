package be.magnias.stahb

import android.app.Application
import be.magnias.stahb.injection.component.AppComponent
import be.magnias.stahb.injection.component.DaggerAppComponent
import be.magnias.stahb.injection.module.DatabaseModule
import be.magnias.stahb.injection.module.NetworkModule

class App : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }
    override fun onCreate() {

        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .databaseModule(DatabaseModule(this))
            .networkModule(NetworkModule())
            .build()
    }
}