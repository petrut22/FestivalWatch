package com.example.festivalwatch.about

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.NavHostFragment
import com.example.festivalwatch.R
import com.example.festivalwatch.login.LoginViewModel


class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_about, container, false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_item -> {
                val action =
                    com.example.festivalwatch.about.AboutFragmentDirections.actionAboutFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}