package com.example.festivalwatch.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home, container, false)

        viewModelFactory = HomeViewModelFactory(
            HomeFragmentArgs.fromBundle(requireArguments()).token!!,
            HomeFragmentArgs.fromBundle(requireArguments()).isAdmin!!,
            HomeFragmentArgs.fromBundle(requireArguments()).username!!
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        binding.homeViewModel = viewModel
        binding.homeFragment = this
        binding.lifecycleOwner = viewLifecycleOwner
        setHasOptionsMenu(true)

        Log.i("HomeFragment", "binding merge bine" + viewModel._token + " " + viewModel.username + " " + viewModel._isAdmin)

        viewModel.navigationToFestivals.observe(viewLifecycleOwner, { navigationToFestivals ->
            if (navigationToFestivals) {
                festivalList()
            }
        })

        viewModel.navigationToProfile.observe(viewLifecycleOwner, { navigationToProfile ->
            if (navigationToProfile) {
                profileEdit()
            }
        })


        return binding.root
    }

    fun festivalList() {
        Log.i("HomeFragment", "navighez catre festivale")
        val action =
            HomeFragmentDirections.actionHomeFragmentToFestivalListFragment(
                viewModel._token,
                viewModel._isAdmin,
                viewModel.username
            )
        NavHostFragment.findNavController(this).navigate(action)
    }


    fun profileEdit() {
        Log.i("HomeFragment", "navighez catre profil")
        val action =
            HomeFragmentDirections.actionHomeFragmentToProfileFragment(
                viewModel._token,
                viewModel._isAdmin,
                viewModel.username
            )
        NavHostFragment.findNavController(this).navigate(action)
    }

    fun festivalEdit() {
        Log.i("HomeFragment", "navighez catre pagina de adaugare a unui festival")
        val action =
            HomeFragmentDirections.actionHomeFragmentToFestivalMakerFragment(
                viewModel._token,
                viewModel._isAdmin,
                viewModel.username
            )
        NavHostFragment.findNavController(this).navigate(action)
    }

    fun addOrganizer() {
        Log.i("HomeFragment", "navighez catre lista de organizatori")
        val action =
            HomeFragmentDirections.actionHomeFragmentToOrganizerFragment(
                viewModel._token,
                viewModel._isAdmin,
                viewModel.username
            )
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
                    HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)
                true
            }
            R.id.about_item -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToAboutFragment()
                NavHostFragment.findNavController(this).navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
