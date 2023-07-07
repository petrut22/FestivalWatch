package com.example.festivalwatch.login

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(false)
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,
            R.layout.fragment_login, container, false)
        Log.d("LoginFragment", "LoginFragment created/re-created!")

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.loginViewModel = viewModel


        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigationState.observe(viewLifecycleOwner, { navigationState ->
            Log.d("LoginFragment", "valoarea este navigationState " + navigationState.toString())
            if (navigationState) {
                Log.d("LoginFragment", "am ajuns aici la register")
                navigateToRegister()
            }
        })

//        viewModel.navigationUser.observe(viewLifecycleOwner, { navigationUser ->
//            Log.d("LoginFragment", "valoarea este navigationUser " + navigationUser.toString())
//            if (navigationUser) {
//                Log.d("LoginFragment", "am ajuns aici la user")
//                navigateToUser()
//            }
//        })

        viewModel.eventLoginSuccess.observe(viewLifecycleOwner, { hasLoggedIn ->
            if (hasLoggedIn) {
                Login()
            }
        })

        viewModel.eventLoginFailed.observe(viewLifecycleOwner, { hasFailed ->
            if (hasFailed) {
                LoginFailed()
            }
        })

        return binding.root
    }

    private fun Login() {
        Log.i("LoginFragment", "login " + viewModel.username.value + " " + viewModel.password.value)
        navigateToHome()
        viewModel.onLoginSuccessComplete()
    }

    private fun LoginFailed() {
        Toast.makeText(activity, "No such email password combination", Toast.LENGTH_LONG).show()
        viewModel.onLoginFailedComplete()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    fun navigateToRegister() {
        val action =
            com.example.festivalwatch.login.LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        NavHostFragment.findNavController(this).navigate(action)
    }

    fun navigateToHome() {
        Log.i("LoginFragment", "login " + viewModel.username.value + " " + viewModel.password.value)

        val action =
            com.example.festivalwatch.login.LoginFragmentDirections.actionLoginFragmentToHomeFragment(
            viewModel.token.value,
            viewModel.isAdmin.value,
            viewModel.username.value)
        NavHostFragment.findNavController(this).navigate(action)
    }

}