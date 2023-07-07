package com.example.festivalwatch.user

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentProfileBinding
import com.example.festivalwatch.home.HomeFragmentDirections
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID


class ProfileFragment : Fragment() {
    val READ_EXTERNAL_STORAGE_REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 2
    private lateinit var viewModel: ProfileViewModel
    private lateinit var viewModelFactory: ProfileViewModelFactory
    var pickedPhoto : Uri? = null
    var pickedBitMap : Bitmap? = null
    var imagePath : String = ""
    private var isEditMode = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        Log.d("ProfileFragment", "ProfileFragment created/re-created!1")
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentProfileBinding>(inflater,
            R.layout.fragment_profile, container, false)

        Log.d("ProfileFragment", "ProfileFragment created/re-created!2")

        viewModelFactory = ProfileViewModelFactory(
            ProfileFragmentArgs.fromBundle(requireArguments()).token!!,
            ProfileFragmentArgs.fromBundle(requireArguments()).isAdmin!!,
            ProfileFragmentArgs.fromBundle(requireArguments()).username!!)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)


        binding.profileViewModel = viewModel
        binding.profileFragment = this
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getData()

        viewModel.eventDataSuccess.observe(viewLifecycleOwner, {success ->
            if (success) {
                binding.usernameId?.text = viewModel.username.value ?: ""
                binding.emailId?.text = viewModel.email.value ?: ""
                binding.phoneId?.text = viewModel.phone.value ?: ""
                binding.countryId?.text = viewModel.country.value ?: ""

                Log.d("ProfileFragment", viewModel.image.value.toString())
                Glide.with(this).load(viewModel.image.value).into(binding.profileId)
                binding.profileId.scaleType = ImageView.ScaleType.CENTER_CROP

                viewModel.getDataComplete()
            }
        })

        viewModel.eventDataFailed.observe(viewLifecycleOwner, {failure ->
            if (failure) {
                Toast.makeText(activity, "Error in getting data, please re-login", Toast.LENGTH_LONG).show()
                viewModel.getDataFailed()
            }
        })



        binding.editButtonId.setOnClickListener {
            isEditMode = !isEditMode

            if (isEditMode) {
                binding.cardViewId.isEnabled = true

                binding.cardViewId.setOnClickListener {
                    pickImageFromLocalStorage()
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                        // request permission if not granted
                        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_REQUEST_CODE)
                    } else {
                        // permission already granted, start gallery intent
                        pickImageFromLocalStorage()
                    }
                }

                binding.switcherEmailId.showNext()
                val oldEmail = viewModel.email.value ?: ""
                binding.emailEditId.setText(oldEmail)

                binding.switcherUsernameId.showNext()
                val oldUsername = viewModel.username.value ?: ""
                binding.usernameEditId.setText(oldUsername)

                binding.switcherPhoneId.showNext()
                val oldPhone = viewModel.phone.value ?: ""
                binding.phoneEditId.setText(oldPhone)

                binding.switcherCountryId.showNext()
                val oldCountry = viewModel.country.value ?: ""
                binding.countryEditId.setText(oldCountry)

                binding.editButtonId.text = "Save"
            } else {
                binding.cardViewId.isEnabled = false
                binding.switcherUsernameId.showNext()
                binding.switcherEmailId.showNext()
                binding.switcherPhoneId.showNext()
                binding.switcherCountryId.showNext()

                viewModel.updateValues(
                    binding.usernameEditId.text.toString(),
                    binding.emailEditId.text.toString(),
                    binding.phoneEditId.text.toString(),
                    binding.countryEditId.text.toString() ,
                )
                binding.editButtonId.text = "Edit"
                //viewModel.postData()

                //extract bitmap from picture field
                Log.d("ProfileFragment", "pickImageFromLocalStorage " + imagePath)
                viewModel.postImageData(imagePath)

//                val action =
//                    ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
//                NavHostFragment.findNavController(this).navigate(action)


            }
        }

        binding.backButtonId.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToHomeFragment(
                    viewModel._token,
                    viewModel._isAdmin,
                    viewModel.username.value)
            NavHostFragment.findNavController(this).navigate(action)
        }

        return binding.root
    }

    private fun pickImageFromLocalStorage() {
        Log.d("ProfileFragment", "pickImageFromLocalStorage")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            // handle the permission request result
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromLocalStorage()
            } else {
                Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            pickedPhoto = data.data
            if (pickedPhoto != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, pickedPhoto!!)
                    pickedBitMap = ImageDecoder.decodeBitmap(source)
                    view?.findViewById<ImageView>(R.id.profileId)?.setImageBitmap(pickedBitMap)
                    view?.findViewById<ImageView>(R.id.profileId)?.scaleType = ImageView.ScaleType.CENTER_CROP

                }
                else {
                    pickedBitMap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,pickedPhoto)
                    view?.findViewById<ImageView>(R.id.profileId)?.setImageBitmap(pickedBitMap)
                    view?.findViewById<ImageView>(R.id.profileId)?.scaleType = ImageView.ScaleType.CENTER_CROP
                }

                imagePath = saveBitmapToInternalStorage(pickedBitMap!!)
                Log.d("ProfileFragment", "Image saved to: $imagePath")
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
                    ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            R.id.about_item -> {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToAboutFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}