package be.magnias.stahb

import android.app.Application
import be.magnias.stahb.injection.component.AppComponent
import be.magnias.stahb.injection.component.DaggerAppComponent
import be.magnias.stahb.injection.module.DatabaseModule
import be.magnias.stahb.injection.module.NetworkModule
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class App : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }
    override fun onCreate() {

        super.onCreate()

        //Setup logging
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
            .methodCount(0)         // (Optional) How many method line to show. Default 2
            .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
            .tag("STAHB")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        appComponent = DaggerAppComponent
            .builder()
            .databaseModule(DatabaseModule(this))
            .networkModule(NetworkModule())
            .build()
    }
}