package com.example.festivalwatch.festivalCustomizer

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentFestivalCustomizerBinding
import com.example.festivalwatch.home.HomeFragmentDirections
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class FestivalCustomizerFragment : Fragment() {

    val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 2
    var pickedPhoto : Uri? = null
    var pickedBitMap : Bitmap? = null
    var imagePathCover : String = ""
    var imagePathDescription : String = ""
    private lateinit var viewModel: FestivalCustomizerViewModel
    private lateinit var viewModelFactory: FestivalCustomizerViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FestivalCustomizerFrag", "FestivalCustomizerFragment created/re-created!")
        val binding = DataBindingUtil.inflate<FragmentFestivalCustomizerBinding>(inflater,
            R.layout.fragment_festival_customizer, container, false)

        viewModelFactory = FestivalCustomizerViewModelFactory(
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).token!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).isAdmin!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).id!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).username!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).title!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).date!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).time!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).latitude!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).longitude!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).address!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).description!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).photoDesc!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).price!!,
            FestivalCustomizerFragmentArgs.fromBundle(requireArguments()).admin!!)

        viewModel = ViewModelProvider(this, viewModelFactory).get(FestivalCustomizerViewModel::class.java)

        binding.festivalCustomizerViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        setHasOptionsMenu(true)


        binding.coverButtonId.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 1)
        }


        binding.descriptionButtonId.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 2)
        }

        binding.saveButtonId.setOnClickListener{
            viewModel.updateFestival(imagePathCover, imagePathDescription)
            val action =
                FestivalCustomizerFragmentDirections.actionFestivalCustomizerFragmentToHomeFragment(
                    viewModel.token,
                    viewModel.isAdmin,
                    viewModel.username
                )
            NavHostFragment.findNavController(this).navigate(action)
        }

        binding.deleteButtonId.setOnClickListener{
            viewModel.deleteFestival()
            val action =
                FestivalCustomizerFragmentDirections.actionFestivalCustomizerFragmentToHomeFragment(
                    viewModel.token,
                    viewModel.isAdmin,
                    viewModel.username
                )
            NavHostFragment.findNavController(this).navigate(action)
        }




        return binding.root
    }

    private fun saveBitmapToInternalStorage(bitmapImage: Bitmap): String {
        val wrapper =
            ContextWrapper(context)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && (requestCode == 1 || requestCode == 2)) {
            val pickedPhoto = data?.data
            if (pickedPhoto != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, pickedPhoto!!)
                    pickedBitMap = ImageDecoder.decodeBitmap(source)
                }
                else {
                    pickedBitMap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, pickedPhoto)
                }

                if (requestCode == 1) {
                    // Set the picked image to cover
                    imagePathCover = saveBitmapToInternalStorage(pickedBitMap!!)
                    Log.d("FestivalCustomizerFrag", "Image saved to: $imagePathCover")
                } else if (requestCode == 2) {
                    // Set the picked image to description
                    imagePathDescription = saveBitmapToInternalStorage(pickedBitMap!!)
                    Log.d("FestivalCustomizerFrag", "Image saved to: $imagePathDescription")
                }

            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_item -> {
                val action =
                    FestivalCustomizerFragmentDirections.actionFestivalCustomizerFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)
                true
            }
            R.id.about_item -> {
                val action =
                    FestivalCustomizerFragmentDirections.actionFestivalCustomizerFragmentToAboutFragment()
                NavHostFragment.findNavController(this).navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
