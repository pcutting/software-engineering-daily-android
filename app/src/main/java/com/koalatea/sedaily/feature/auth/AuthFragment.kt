package com.koalatea.sedaily.feature.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.koalatea.sedaily.MainActivity
import com.koalatea.sedaily.R
import com.koalatea.sedaily.databinding.FragmentAuthBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.HttpException

class AuthFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentAuthBinding>(
                inflater, R.layout.fragment_auth, container, false
        ).apply {
            viewModel = authViewModel

            lifecycleOwner = this@AuthFragment

            loginRegButton.setOnClickListener {
                loginRegButton.isEnabled = false
                val usernameString = username.text.toString()
                val passwordString = password.text.toString()
                val emailString = email.text.toString()
                loginRegButton.isEnabled = false

                viewModel?.authenticate(usernameString, passwordString, emailString)
            }

            forgotPassword.setOnClickListener {
                forgotPasswordClick()
            }
        }

        authViewModel.userToken.observe(this, Observer {
            // @TODO: Nav back?
            val intent = Intent(this.activity, MainActivity::class.java)
            startActivity(intent)
        })

        authViewModel.authError.observe(this, Observer { error ->
            binding.loginRegButton.isEnabled = true
            handleLoginError(error)
        })

        return binding.root
    }

    private fun handleLoginError(error: Throwable) {
        try {
            // We had non-200 http error
            if (error is HttpException) {
                val response = error.response()
                displayMessage(response.errorBody()?.string() as String)
            } else {
                displayMessage(error.message as String)
            }
        } catch (e: Exception) {
            displayMessage(e.message as String)
        }
    }

    private fun displayMessage(message: String) {
        AlertUtil.displayMessage(this.context as Context, message)
    }

    private fun forgotPasswordClick() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.softwaredaily.com/forgot-password"))
        startActivity(browserIntent)
    }
}