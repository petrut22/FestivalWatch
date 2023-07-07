package com.example.festivalwatch.festivalList

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.festivalwatch.FestivalListAdapter
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentFestivalListBinding
import com.example.festivalwatch.festivalCustomizer.FestivalCustomizerFragmentDirections
import com.example.festivalwatch.festivalMaker.FestivalMakerFragmentDirections

class FestivalListFragment : Fragment() {
    private lateinit var viewModel: FestivalListViewModel
    private lateinit var viewModelFactory: FestivalListViewModelFactory
    private lateinit var festivalsRecyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<FestivalListAdapter.FestivalListViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("FestivalListFragment", "FestivalListFragment started!")
        val binding = DataBindingUtil.inflate<FragmentFestivalListBinding>(inflater, R.layout.fragment_festival_list, container, false)
        binding.festivalListFragment = this

        viewModelFactory = FestivalListViewModelFactory(
            FestivalListFragmentArgs.fromBundle(requireArguments()).token!!,
            FestivalListFragmentArgs.fromBundle(requireArguments()).isAdmin!!,
            FestivalListFragmentArgs.fromBundle(requireArguments()).username!!)

        Log.d("FestivalListFragment", "FestivalListFragment started2!" )
        viewModel = ViewModelProvider(this, viewModelFactory).get(FestivalListViewModel::class.java)
        Log.d("FestivalListFragment", "ViewModelProvider started!")


        festivalsRecyclerView = binding.recyclerView
        festivalsRecyclerView.layoutManager = LinearLayoutManager(context)
        festivalsRecyclerView.setHasFixedSize(true)

        Log.d("FestivalListFragment", "festivalsRecyclerView = binding.recyclerView")
        //To DO
        adapter = FestivalListAdapter(viewModel.festivalsArrayList, viewModel.token, viewModel.isAdmin, viewModel.username, this)

        festivalsRecyclerView.adapter = adapter

        viewModel.getData()

        viewModel.getDataSuccess.observe(viewLifecycleOwner, {
            if (it) {
                adapter.notifyDataSetChanged()
                viewModel.getDataComplete()
            }
        })

        fixBackToHomeFragment()
        setHasOptionsMenu(true)
        return binding.root
    }

    fun fixBackToHomeFragment() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Replace action_id with the id of the action or the destination you want to navigate to
                    val action =
                        FestivalListFragmentDirections.actionFestivalListFragmentToHomeFragment(
                            viewModel.token,
                            viewModel.isAdmin,
                            viewModel.username
                        )
                    NavHostFragment.findNavController(this@FestivalListFragment).navigate(action)

                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_item -> {
                val action =
                    FestivalListFragmentDirections.actionFestivalListFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            R.id.about_item -> {
                val action =
                    FestivalListFragmentDirections.actionFestivalListFragmentToAboutFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }











}