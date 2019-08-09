package be.magnias.stahb.injection.component

import be.magnias.stahb.App
import be.magnias.stahb.adapter.TabListPagerAdapter
import be.magnias.stahb.injection.module.DatabaseModule
import be.magnias.stahb.injection.module.NetworkModule
import be.magnias.stahb.network.ServiceInterceptor
import be.magnias.stahb.persistence.TabRepository
import be.magnias.stahb.persistence.UserRepository
import be.magnias.stahb.persistence.UserService
import be.magnias.stahb.ui.viewmodel.*
import dagger.Component
import javax.inject.Singleton

/**
 * Component providing inject() methods for presenters.
 */
@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface AppComponent {
    /**
     * Injects required dependencies into the specified PostListViewModel.
     * @param postListViewModel PostListViewModel in which to inject the dependencies
     */
    fun inject(app: App)
    fun inject(tabViewModel: TabViewModel)
    fun inject(tabListViewModel: TabListViewModel)
    fun inject(tabListFavoritesViewModel: TabListFavoritesViewModel)
    fun inject(tabRepository: TabRepository)
    fun inject(mainViewModel: MainViewModel)
    fun inject(loginViewModel: LoginViewModel)
    fun inject(userService: UserService)
    fun inject(serviceInterceptor: ServiceInterceptor)
    fun inject(userRepository: UserRepository)
    fun inject(tabListPagerAdapter: TabListPagerAdapter)
    fun inject(tabOverviewViewModel: TabOverviewViewModel)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        fun networkModule(networkModule: NetworkModule): Builder

        fun databaseModule(databaseModule: DatabaseModule): Builder
    }
}