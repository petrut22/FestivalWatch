package com.example.festivalwatch.organizer

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentHomeBinding
import com.example.festivalwatch.databinding.FragmentOrganizerBinding
import com.example.festivalwatch.home.HomeFragmentArgs
import com.example.festivalwatch.home.HomeFragmentDirections
import com.example.festivalwatch.home.HomeViewModel
import com.example.festivalwatch.home.HomeViewModelFactory

class OrganizerFragment : Fragment() {
    private lateinit var viewModel: OrganizerViewModel
    private lateinit var viewModelFactory: OrganizerViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentOrganizerBinding>(
            inflater,
            R.layout.fragment_organizer,
            container,
            false
        )

        viewModelFactory = OrganizerViewModelFactory(
            OrganizerFragmentArgs.fromBundle(requireArguments()).token!!,
            OrganizerFragmentArgs.fromBundle(requireArguments()).isAdmin!!,
            OrganizerFragmentArgs.fromBundle(requireArguments()).username!!
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(OrganizerViewModel::class.java)

        binding.organizerViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setHasOptionsMenu(true)

        Log.i(
            "OrganizerFragment",
            "binding merge bine" + viewModel._token + " " + viewModel.username + " " + viewModel._isAdmin
        )

        viewModel.getData()

        viewModel.eventOrganizerSuccess.observe(viewLifecycleOwner, { hasSucceed ->
            if (hasSucceed) {
                navigateToHome()
            }
        })

        viewModel.eventOrganizerFailed.observe(viewLifecycleOwner, { hasFailed ->
            if (hasFailed) {
                AddedFailed()
            }
        })

        return binding.root
    }

    private fun AddedFailed() {
        Toast.makeText(activity, "The user cannot be added", Toast.LENGTH_LONG).show()
        viewModel.getOrganizerFailedComplete()
    }

    private fun navigateToHome() {
        Log.i("OrganizerFragment", viewModel.username + " " + viewModel._isAdmin)

        val action =
            OrganizerFragmentDirections.actionOrganizerFragmentToHomeFragment(
                viewModel._token,
                viewModel._isAdmin,
                viewModel.username)
        NavHostFragment.findNavController(this).navigate(action)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_item -> {
                val action =
                    OrganizerFragmentDirections.actionOrganizerFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)
                true
            }
            R.id.about_item -> {
                val action =
                    OrganizerFragmentDirections.actionOrganizerFragmentToAboutFragment()
                NavHostFragment.findNavController(this).navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}