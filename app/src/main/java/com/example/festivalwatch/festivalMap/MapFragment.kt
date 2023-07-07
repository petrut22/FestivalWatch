package com.example.festivalwatch.festivalMap

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.festivalwatch.R
import com.example.festivalwatch.festivalList.FestivalListViewModelFactory
import com.example.festivalwatch.festivalMenu.FestivalMenuFragmentArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    private lateinit var viewModel: MapViewModel
    private lateinit var viewModelFactory: MapViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = MapViewModelFactory(
            requireActivity().application,
            MapFragmentArgs.fromBundle(requireArguments()).token!!,
            MapFragmentArgs.fromBundle(requireArguments()).festivalName!!,
            MapFragmentArgs.fromBundle(requireArguments()).username!!,
            MapFragmentArgs.fromBundle(requireArguments()).latitude!!,
            MapFragmentArgs.fromBundle(requireArguments()).longitude!!)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)
        viewModel.initMap(childFragmentManager, R.id.map)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_item -> {
                val action =
                    MapFragmentDirections.actionMapFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            R.id.about_item -> {
                val action =
                    MapFragmentDirections.actionMapFragmentToAboutFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}