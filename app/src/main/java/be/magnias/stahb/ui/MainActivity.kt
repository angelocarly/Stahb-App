package be.magnias.stahb.ui

import android.os.Bundle
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


class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        //Display toast error message on refresh failure
        viewModel.getRefreshLoadingVisibility().observe(this, Observer { r ->
            if (r.status == Status.ERROR) {
                Toast.makeText(applicationContext, r.message, Toast.LENGTH_LONG).show()
            }
        })

        //Setup fragment
        if (savedInstanceState == null) {
            val fragment = TabOverviewFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

    }

    // Display a tab via id
    fun showTab(id: String) {
        val fragment = TabFragment.newInstance(id)
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("tabs")
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun showLogin() {
        val fragment = LoginFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("register")
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun showRegister() {
        val fragment = RegisterFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("register")
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun logout() {
        viewModel.logout()
    }

    fun refreshTabs() {
        viewModel.refreshTabs()
    }
}