package com.example.festivalwatch.festivalMenu

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentFestivalMenuBinding
import com.example.festivalwatch.festivalList.FestivalListFragmentDirections
import com.example.festivalwatch.home.HomeFragmentArgs

class FestivalMenuFragment : Fragment() {
    private lateinit var viewModel: FestivalMenuViewModel
    private lateinit var viewModelFactory: FestivalMenuViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("FestivalMenuFragment", "FestivalMenuFragment created/re-created!")
        val binding = DataBindingUtil.inflate<FragmentFestivalMenuBinding>(inflater,
            R.layout.fragment_festival_menu, container, false)
        Log.d("FestivalMenuFragment", "1")
        viewModelFactory = FestivalMenuViewModelFactory(
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).token!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).isAdmin!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).username!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).title!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).date!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).time!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).latitude!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).longitude!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).address!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).description!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).photoDesc!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).price!!,
            FestivalMenuFragmentArgs.fromBundle(requireArguments()).admin!!)
        Log.d("FestivalMenuFragment", "2")
        viewModel = ViewModelProvider(this, viewModelFactory).get(FestivalMenuViewModel::class.java)
        Log.d("FestivalMenuFragment", "3")
        binding.festivalMenuViewModel = viewModel

        viewModel.getQRData()


        binding.lifecycleOwner = viewLifecycleOwner

        //val buttonMenu = view.findViewById<CardView>(R.id.festival_menu_id)
        Log.d("FestivalMenuFragment", "The view is inflated and the buttonMenu was binded")

        val buttonMenu = binding.festivalMenuId
        val buttonProfile = binding.festivalProfileId
        val buttonLocation = binding.festivalLocationId
        val buttonTicket = binding.festivalPriceId
        val buttonNotify = binding.selectHeartId
        Glide.with(this).load(viewModel.photoDesc.value).into(binding.imageViewFestivalId)

        buttonMenu.setOnClickListener {
            val action =
                FestivalMenuFragmentDirections.actionFestivalMenuFragmentToFestivalListFragment(
                    viewModel.token,
                    viewModel.isAdmin,
                    viewModel.username)
            NavHostFragment.findNavController(this).navigate(action)
        }

        buttonProfile.setOnClickListener {
            val action =
                FestivalMenuFragmentDirections.actionFestivalMenuFragmentToProfileFragment(
                    viewModel.token,
                    viewModel.isAdmin,
                    viewModel.username)
            NavHostFragment.findNavController(this).navigate(action)
        }

        buttonLocation.setOnClickListener {
            val action =
                FestivalMenuFragmentDirections.actionFestivalMenuFragmentToMapFragment(
                    viewModel.token,
                    viewModel.title.value!!,
                    viewModel.username,
                    viewModel.latitude.value!!,
                    viewModel.longitude.value!!
                )
            NavHostFragment.findNavController(this).navigate(action)
        }
        viewModel.eventDataSuccess.observe(viewLifecycleOwner, {success ->
            if (success) {
                viewModel.getDataComplete()
                Log.d("FestivalMenuFragment", "nebunia lui salam " + viewModel.qr_code.value!!)
                buttonTicket.setOnClickListener {
                    Log.d("FestivalMenuFragment", "nebunia lui salamw " + viewModel.qr_code.value!!)
                    val action =
                        FestivalMenuFragmentDirections.actionFestivalMenuFragmentToTicketQRFragment(
                            viewModel.token,
                            viewModel.username,
                            viewModel.title.value!!,
                            viewModel.date.value!!,
                            viewModel.time.value!!,
                            viewModel.price.value!!,
                            viewModel.qr_code.value!!,
                            viewModel.photoDesc.value!!,
                        )
                    NavHostFragment.findNavController(this).navigate(action)
                }

            }
        })

        viewModel.eventDataFailed.observe(viewLifecycleOwner, {failure ->
            if (failure) {
                Log.d("FestivalMenuFragment", "sad:( lui salam ")
                viewModel.getDataFailed()
                buttonTicket.setOnClickListener {
                    val action =
                        FestivalMenuFragmentDirections.actionFestivalMenuFragmentToTicketFragment(
                            viewModel.token,
                            viewModel.username,
                            viewModel.title.value!!,
                            viewModel.date.value!!,
                            viewModel.time.value!!,
                            viewModel.price.value!!,
                            viewModel.photoDesc.value!!,
                        )
                    NavHostFragment.findNavController(this).navigate(action)
                }
            }
        })


        var isPressed = false

        buttonNotify.setOnClickListener {
            Log.d("FestivalMenuFragment", "Notify clicked!")
            isPressed = !isPressed
            if (isPressed) {
                buttonNotify.setImageResource(R.drawable.ic_filled_heart)
            } else {
                buttonNotify.setImageResource(R.drawable.ic_heart)
            }

        }

        setHasOptionsMenu(true)
        return binding.root

    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_item -> {
                val action =
                    FestivalMenuFragmentDirections.actionFestivalMenuFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            R.id.about_item -> {
                val action =
                    FestivalMenuFragmentDirections.actionFestivalMenuFragmentToAboutFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}