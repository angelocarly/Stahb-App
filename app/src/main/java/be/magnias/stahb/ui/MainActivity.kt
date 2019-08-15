package be.magnias.stahb.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.magnias.stahb.R
import be.magnias.stahb.model.Status
import be.magnias.stahb.ui.fragment.LoginFragment
import be.magnias.stahb.ui.fragment.RegisterFragment
import be.magnias.stahb.ui.fragment.TabFragment
import be.magnias.stahb.ui.fragment.TabOverviewFragment
import be.magnias.stahb.ui.viewmodel.MainViewModel
import com.orhanobut.logger.Logger
import io.reactivex.disposables.Disposable
import be.magnias.stahb.R.id.details_pane as details_pane1

/**
 * The Main Activity of the application.
 */
class MainActivity : AppCompatActivity() {

    private var isTwoPane: Boolean = false

    private lateinit var viewModel: MainViewModel

    private var refreshSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize viewModel
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        if (resources.getBoolean(R.bool.isTablet)) {
            if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                isTwoPane = true
            }
        }

        // Set the TabOverviewFragment as content
        if (savedInstanceState == null) {
            val fragment = TabOverviewFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        // Show toast on failed load
        refreshSubscription = viewModel.getRefreshLoadingVisibility().subscribe({
            if(it.status == Status.ERROR) {
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
            } else if (it?.data == true) {
                Toast.makeText(applicationContext, "Refreshed tabs!", Toast.LENGTH_LONG).show()
                Logger.d("HERERERERER")
            }
        }, {
            Logger.e(it.message.toString())
        })

    }

    /**
     * Open a fragment showing a tab view.
     * @param id The id of the requested Tab.
     */
    fun showTab(id: String) {
        if(isTwoPane) {
            val fragment = TabFragment.newInstance(id)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_pane, fragment)
                .commit()
        }
        else {
            val fragment = TabFragment.newInstance(id)
            supportFragmentManager
                .beginTransaction()
                .addToBackStack("tab")
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    /**
     * Show a fragment to login a user
     */
    fun showLogin() {
        val fragment = LoginFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("login")
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Show a fragment to register a new user.
     */
    fun showRegister() {
        val fragment = RegisterFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("register")
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Logout the user
     */
    fun logout() {
        viewModel.logout()
    }

    override fun onDestroy() {
        super.onDestroy()
        refreshSubscription?.dispose()
    }
}