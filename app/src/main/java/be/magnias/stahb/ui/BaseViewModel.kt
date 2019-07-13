package be.magnias.stahb.ui

import androidx.lifecycle.ViewModel
import be.magnias.stahb.injection.component.DaggerViewModelInjectorComponent
import be.magnias.stahb.injection.component.ViewModelInjectorComponent
import be.magnias.stahb.injection.module.NetworkModule

abstract class BaseViewModel: ViewModel(){

    private val injector: ViewModelInjectorComponent = DaggerViewModelInjectorComponent
        .builder()
        .networkModule(NetworkModule)
        .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is TabViewModel -> injector.inject(this)
        }
    }
}