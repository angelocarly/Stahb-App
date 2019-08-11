package be.magnias.stahb

import android.app.Application
import be.magnias.stahb.injection.component.AppComponent
import be.magnias.stahb.injection.component.DaggerAppComponent
import be.magnias.stahb.injection.module.DatabaseModule
import be.magnias.stahb.injection.module.NetworkModule
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

/**
 * Starting point of the application
 */
class App : Application() {

    companion object {
        /**
         * Public Dagger AppComponent.
         * Is used to inject services.
         */
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {

        super.onCreate()

        // Setup Orhanobut logging
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(2)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("STAHB")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        // Initialize appcomponent
        appComponent = DaggerAppComponent
            .builder()
            .databaseModule(DatabaseModule(this))
            .networkModule(NetworkModule())
            .build()
    }
}