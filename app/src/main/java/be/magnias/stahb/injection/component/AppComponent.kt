package be.magnias.stahb.injection.component

import be.magnias.stahb.App
import be.magnias.stahb.adapter.TabListPagerAdapter
import be.magnias.stahb.injection.module.DatabaseModule
import be.magnias.stahb.injection.module.NetworkModule
import be.magnias.stahb.network.ServiceInterceptor
import be.magnias.stahb.service.TabService
import be.magnias.stahb.persistence.UserRepository
import be.magnias.stahb.service.UserService
import be.magnias.stahb.ui.viewmodel.*
import dagger.Component
import javax.inject.Singleton

/**
 * Component providing general modules for the application
 */
@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface AppComponent {

    /**
     * Injects required dependencies into the specified App.
     * @param app App in which to inject the dependencies
     */
    fun inject(app: App)

    /**
     * Injects required dependencies into the specified tabViewModel.
     * @param tabViewModel TabViewModel in which to inject the dependencies
     */
    fun inject(tabViewModel: TabViewModel)

    /**
     * Injects required dependencies into the specified TabListViewModel.
     * @param tabListViewModel TabListViewModel in which to inject the dependencies
     */
    fun inject(tabListViewModel: TabListViewModel)

    /**
     * Injects required dependencies into the specified TabListFavoritesViewModel.
     * @param tabListFavoritesViewModel TabListFavoritesViewModel in which to inject the dependencies
     */
    fun inject(tabListFavoritesViewModel: TabListFavoritesViewModel)

    /**
     * Injects required dependencies into the specified TabService.
     * @param tabService TabService in which to inject the dependencies
     */
    fun inject(tabService: TabService)

    /**
     * Injects required dependencies into the specified MainViewModel.
     * @param mainViewModel MainViewModel in which to inject the dependencies
     */
    fun inject(mainViewModel: MainViewModel)

    /**
     * Injects required dependencies into the specified LoginViewModel.
     * @param loginViewModel LoginViewModel in which to inject the dependencies
     */
    fun inject(loginViewModel: LoginViewModel)

    /**
     * Injects required dependencies into the specified UserService.
     * @param userService UserService in which to inject the dependencies
     */
    fun inject(userService: UserService)

    /**
     * Injects required dependencies into the specified ServiceInterceptor.
     * @param serviceInterceptor App in which to inject the dependencies
     */
    fun inject(serviceInterceptor: ServiceInterceptor)

    /**
     * Injects required dependencies into the specified UserRepository.
     * @param userRepository UserRepository in which to inject the dependencies
     */
    fun inject(userRepository: UserRepository)

    /**
     * Injects required dependencies into the specified TabListPagerAdapter.
     * @param tabListPagerAdapter TabListPagerAdapter in which to inject the dependencies
     */
    fun inject(tabListPagerAdapter: TabListPagerAdapter)

    /**
     * Injects required dependencies into the specified TabOverviewViewModel.
     * @param tabOverviewViewModel TabOverviewViewModel in which to inject the dependencies
     */
    fun inject(tabOverviewViewModel: TabOverviewViewModel)

    /**
     * Injects required dependencies into the specified RegisterViewModel.
     * @param registerViewModel RegisterViewModel in which to inject the dependencies
     */
    fun inject(registerViewModel: RegisterViewModel)

    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        /**
         * Provide a NetworkModule to inject into classes.
         * @param networkModule NetworkModule to inject
         */
        fun networkModule(networkModule: NetworkModule): Builder

        /**
         * Provide a DatabaseModule to inject into classes.
         * @param databaseModule DatabaseModule to inject
         */
        fun databaseModule(databaseModule: DatabaseModule): Builder
    }
}