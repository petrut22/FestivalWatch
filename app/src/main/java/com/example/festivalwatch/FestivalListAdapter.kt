package com.example.festivalwatch

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.festivalwatch.festivalList.FestivalListFragmentDirections

class FestivalListAdapter(private val festivalArray : ArrayList<FestivalClass>, private val token: String, private val isAdmin: String, private val username: String,  private val fragment: Fragment) : RecyclerView.Adapter<FestivalListAdapter.FestivalListViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalListViewHolder {
        val festivalView = LayoutInflater.from(parent.context).inflate(R.layout.festival_element, parent, false)

        return FestivalListViewHolder(festivalView)
    }

    override fun onBindViewHolder(holder: FestivalListViewHolder, index: Int) {
        val currentFestival = festivalArray[index]
        holder.name.text = currentFestival.name
        holder.date.text = currentFestival.date
        Log.i("FestivalListAdapter ", currentFestival.photoCover)

        Glide.with(fragment.requireContext()).load(currentFestival.photoCover).into(holder.photoCover)


        if(username == currentFestival.admin && isAdmin == "true") {
            holder.buttonEditFestival.setVisibility(View.VISIBLE);
            holder.buttonEditFestival.setOnClickListener {
                Log.i("FestivalListAdapter ", "Go to Customizer Fragment")
                val action = FestivalListFragmentDirections.actionFestivalListFragmentToFestivalCustomizerFragment(
                    token,
                    isAdmin,
                    currentFestival.id,
                    currentFestival.name,
                    currentFestival.date,
                    currentFestival.time,
                    currentFestival.latitude,
                    currentFestival.longitude,
                    currentFestival.address,
                    currentFestival.description,
                    currentFestival.photoDesc,
                    currentFestival.admin,
                    currentFestival.price,
                    username)
                NavHostFragment.findNavController(fragment).navigate(action)
            }

        } else {
            holder.buttonEditFestival.setVisibility(View.GONE);
        }

        holder.buttonGoToFestival.setOnClickListener {
            val action = FestivalListFragmentDirections.actionFestivalListFragmentToFestivalMenuFragment(
                token,
                isAdmin,
                currentFestival.name,
                currentFestival.date,
                currentFestival.time,
                currentFestival.latitude,
                currentFestival.longitude,
                currentFestival.address,
                currentFestival.description,
                currentFestival.photoDesc,
                currentFestival.admin,
                currentFestival.price,
                username)
            NavHostFragment.findNavController(fragment).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return festivalArray.size
    }

    class FestivalListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val name : TextView = itemView.findViewById(R.id.festival_name_id)
        val date : TextView = itemView.findViewById(R.id.festival_date_id)
        val photoCover : ImageView = itemView.findViewById(R.id.festival_image_id)
        val buttonGoToFestival = itemView.findViewById<Button>(R.id.festival_button_id)
        val buttonEditFestival = itemView.findViewById<Button>(R.id.festival_edit_button_id)

        override fun onClick(v: View?) {
            Log.i("FestivalListAdapter", "I clicked on Festival Adapter")
        }

    }
}