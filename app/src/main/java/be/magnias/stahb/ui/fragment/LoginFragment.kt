package be.magnias.stahb.ui.fragment

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import be.magnias.stahb.R
import be.magnias.stahb.model.Status
import be.magnias.stahb.ui.MainActivity
import be.magnias.stahb.ui.viewmodel.LoginViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

/**
 * The login fragment.
 * In this fragment a user is requested to login using his username and password.
 */
class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init viewmodel
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        // Show the correct views if the viewmodel is logging in
        loginViewModel.getLoadingVisibility().observe(this, Observer {
            if (it) {
                //Show the loading bar
                loading_panel.visibility = View.VISIBLE
                button_login.visibility = View.GONE
                text_view_error.visibility = View.GONE
            } else {
                //Dont show the loading bar
                loading_panel.visibility = View.GONE
                button_login.visibility = View.VISIBLE
            }
        })

        // Update the UI when the viewmodel finishes/fails logging in
        loginViewModel.getLoginResult().observe(this, Observer {
            if (it.status == Status.ERROR) {
                //Display an error message
                text_view_error.text = it.message
                text_view_error.visibility = View.VISIBLE
            } else if (it.status == Status.SUCCESS) {
                //Leave the register fragment
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Login button listener
        view.button_login.setOnClickListener {
            val usernameText = view.textinput_username.text.toString()
            val passwordText = view.textinput_password.text.toString()

            // Verify the input
            var errors = false
            if (usernameText.isBlank()) {
                textinput_username.error = "Username can't be blank"
                errors = true
            }
            if (passwordText.isBlank()) {
                textinput_password.error = "Password can't be blank"
                errors = true
            }

            // Log the user in
            if (!errors) loginViewModel.login(usernameText, passwordText)
        }

        // Register button listener
        view.button_register.setOnClickListener {
            Logger.d("Showing register fragment")
            (activity as MainActivity).showRegister()
        }

        return view
    }

    // Instance method
    companion object {
        /**
         * Create a new instance of LoginFragment
         */
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}
