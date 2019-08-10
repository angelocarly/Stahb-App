package be.magnias.stahb.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import be.magnias.stahb.R
import be.magnias.stahb.model.Status
import be.magnias.stahb.ui.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*

class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Init viewmodel
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        registerViewModel.getRegisterResult().observe(this, Observer {
            if (it.status == Status.ERROR) {
                text_view_error.text = it.message
                text_view_error.visibility = View.VISIBLE
            } else if (it.status == Status.SUCCESS) {
                //Leave the register fragment
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
        })

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        view.button_register.setOnClickListener {

            val usernameText = view.textinput_username.text.toString()
            val passwordText = view.textinput_password.text.toString()
            val repeatPasswordText = view.textinput_repeatpassword.text.toString()

            //Verify the input
            var errors = false
            if (usernameText.isNullOrBlank()) {
                textinput_username.error = "Username can't be blank"
                errors = true
            }
            if (passwordText.isNullOrBlank()) {
                textinput_password.error = "Password can't be blank"
                errors = true
            }
            if (repeatPasswordText.isNullOrBlank()) {
                textinput_repeatpassword.error = "Repeat Password can't be blank"
                errors = true
            }
            if (passwordText != repeatPasswordText) {
                textinput_repeatpassword.error = "Passwords do not match"
                errors = true
            }

            if (!errors) registerViewModel.register(
                usernameText,
                passwordText
            )
        }

        return view
    }

    //Instance method
    companion object {
        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}
