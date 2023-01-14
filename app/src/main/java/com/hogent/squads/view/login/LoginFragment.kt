package com.hogent.squads.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.hogent.squads.R
import com.hogent.squads.databinding.FragmentLoginBinding
import com.hogent.squads.model.domain.CredentialsValidator.Companion.validatePassword
import com.hogent.squads.model.domain.CredentialsValidator.Companion.validateUsername
import com.hogent.squads.model.network.rest.resources.LoginRequest
import com.hogent.squads.model.network.rest.resources.UserCredentials
import com.hogent.squads.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        setUpObservers()
        setLoginHandler()
        setCredentialsValidationHandler()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpObservers () {
        userViewModel.activeUser.observe(viewLifecycleOwner) { foundUser ->
            if (foundUser != null) {
                binding.usernameInputField.error = null
                binding.passwordTextLayout.error = null
                navController.popBackStack()
            }
        }
    }

    private fun setLoginHandler() {
        binding.loginButton.setOnClickListener {
            if (!validateUsername(binding.usernameInputField.text.toString())) {
                binding.usernameTextLayout.error = getString(R.string.app_username_required)
            } else if (!validatePassword(binding.passwordInputField.text.toString())) {
                binding.passwordTextLayout.error = getString(R.string.app_password_format_invalid)
            } else {
                attemptLogin(
                    binding.usernameInputField.text.toString(),
                    binding.passwordInputField.text.toString()
                )
            }
        }
    }

    private fun attemptLogin(username: String, password: String) {
        userViewModel.login(LoginRequest(UserCredentials(username, password)), binding.loginScrollView)
    }

    private fun setCredentialsValidationHandler() {
        binding.usernameInputField.setOnKeyListener { _, _, _ ->
            if (validateUsername(binding.usernameInputField.text.toString())) {
                binding.usernameTextLayout.error = null
            }
            false
        }
        binding.passwordInputField.setOnKeyListener { _, _, _ ->
            if (validatePassword(binding.passwordInputField.text.toString())) {
                binding.passwordTextLayout.error = null
            }
            false
        }
    }
}
