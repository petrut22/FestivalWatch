package com.example.festivalwatch.register

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.festivalwatch.MainActivity
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentLoginBinding
import com.example.festivalwatch.databinding.FragmentRegisterBinding
import com.example.festivalwatch.login.LoginViewModel


class RegisterFragment : Fragment() {
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(false)
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentRegisterBinding>(inflater,
            R.layout.fragment_register, container, false)

        Log.d("RegisterFragment", "RegisterFragment created/re-created!")

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.registerViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        Log.d("RegisterFragment", "1")
        viewModel.navigationState.observe(viewLifecycleOwner, { navigationState ->
            Log.d("RegisterFragment", "valoarea este " + navigationState.toString())
            if (navigationState) {
                Log.d("RegisterFragment", "am ajuns aici")
                navigateToLogin()
            }
        })

        viewModel.eventRegisterSuccess.observe(viewLifecycleOwner, { hasLoggedIn ->
            if (hasLoggedIn) {
                Register()
            }
        })

        viewModel.eventRegisterFailed.observe(viewLifecycleOwner, { hasFailed ->
            if (hasFailed) {
                RegisterFailed()
            }
        })

        Log.d("RegisterFragment", "2")
        return binding.root
    }

    private fun Register() {
        Log.i("RegisterViewModel", "register " + viewModel.email.value
                + " " + viewModel.username.value + " " + viewModel.password.value + " " + viewModel.phone.value + " " + viewModel.country.value)

        navigateToLogin()
        viewModel.onRegisterSuccessComplete()
    }

    private fun RegisterFailed() {
        Toast.makeText(activity, "Register failed", Toast.LENGTH_LONG).show()
        viewModel.onRegisterFailedComplete()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    fun navigateToLogin() {

        val action =
            com.example.festivalwatch.register.RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        NavHostFragment.findNavController(this).navigate(action)
    }

}