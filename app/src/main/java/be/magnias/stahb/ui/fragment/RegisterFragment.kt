package be.magnias.stahb.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import be.magnias.stahb.R
import be.magnias.stahb.model.Status
import be.magnias.stahb.ui.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*

/**
 * The register fragment.
 * In this fragment a user is requested to register a new account using a username and a password.
 */
class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init viewmodel
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        // Show the correct views if the viewmodel is registering
        registerViewModel.getLoadingVisibility().observe(this, Observer {
            if (it) {
                //Show the loading bar
                loading_panel.visibility = View.VISIBLE
                button_register.visibility = View.GONE
                text_view_error.visibility = View.GONE
            } else {
                //Dont show the loading bar
                loading_panel.visibility = View.GONE
                button_register.visibility = View.VISIBLE
            }
        })

        // Update the UI when the viewmodel finishes registering
        registerViewModel.getRegisterResult().observe(this, Observer {
            if (it.status == Status.ERROR) {
                // Display an error message
                text_view_error.text = it.message
                text_view_error.visibility = View.VISIBLE
            } else if (it.status == Status.SUCCESS) {
                Toast.makeText(context, "Succesfully registered!", Toast.LENGTH_LONG).show()

                // Leave the register fragment
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Registration button listener
        view.button_register.setOnClickListener {

            val usernameText = view.textinput_username.text.toString()
            val passwordText = view.textinput_password.text.toString()
            val repeatPasswordText = view.textinput_repeatpassword.text.toString()

            // Verify the user input
            var errors = false
            if (usernameText.isBlank()) {
                textinput_username.error = "Username can't be blank"
                errors = true
            }
            if (passwordText.isBlank()) {
                textinput_password.error = "Password can't be blank"
                errors = true
            }
            if (repeatPasswordText.isBlank()) {
                textinput_repeatpassword.error = "Repeat Password can't be blank"
                errors = true
            }
            if (passwordText != repeatPasswordText) {
                textinput_repeatpassword.error = "Passwords do not match"
                errors = true
            }

            // Try to register the user
            if (!errors) registerViewModel.register(
                usernameText,
                passwordText
            )
        }

        return view
    }

    //Instance method
    companion object {
        /**
         * Create a new instance of the RegisterFragment
         */
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}
