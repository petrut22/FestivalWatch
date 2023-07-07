package com.example.festivalwatch.ticketQR

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentTicketBinding
import com.example.festivalwatch.databinding.FragmentTicketQRBinding
import com.example.festivalwatch.ticket.TicketFragmentArgs
import com.example.festivalwatch.ticket.TicketViewModel
import com.example.festivalwatch.ticket.TicketViewModelFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.wallet.PaymentData
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONException
import org.json.JSONObject

class TicketQRFragment : Fragment() {

        private lateinit var viewModel: TicketQRViewModel
        private lateinit var viewModelFactory: TicketQRViewModelFactory
        private lateinit var imageQR: ImageView

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            Log.d("TicketQRFragment", "TicketQRFragment created/re-created!")
            // Inflate the layout for this fragment

            viewModelFactory = TicketQRViewModelFactory(
                TicketQRFragmentArgs.fromBundle(requireArguments()).token!!,
                TicketQRFragmentArgs.fromBundle(requireArguments()).username!!,
                TicketQRFragmentArgs.fromBundle(requireArguments()).title!!,
                TicketQRFragmentArgs.fromBundle(requireArguments()).date!!,
                TicketQRFragmentArgs.fromBundle(requireArguments()).time!!,
                TicketQRFragmentArgs.fromBundle(requireArguments()).price!!,
                TicketQRFragmentArgs.fromBundle(requireArguments()).qrCode!!,
                TicketQRFragmentArgs.fromBundle(requireArguments()).photoDesc!!)
            Log.d("TicketQRFragment", "TicketQRFragment binding created/re-created!")

            viewModel = ViewModelProvider(this, viewModelFactory).get(TicketQRViewModel::class.java)

            val binding = DataBindingUtil.inflate<FragmentTicketQRBinding>(inflater,
                R.layout.fragment_ticket_q_r, container, false)

            binding.ticketQRViewModel = viewModel

            binding.lifecycleOwner = viewLifecycleOwner

            imageQR = binding.festivalPayId

            val qrCodeBitmap = generateQRCode(viewModel.qr_code.value!!)

            imageQR.setImageBitmap(qrCodeBitmap)

            Glide.with(this).load(viewModel.photoDesc.value).into(binding.imageViewFestivalId)

            setHasOptionsMenu(true)

            return binding.root
        }

        fun generateQRCode(text: String): Bitmap {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        }


        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(R.menu.menu, menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.logout_item -> {
                    val action =
                        TicketQRFragmentDirections.actionTicketQRFragmentToLoginFragment()
                    NavHostFragment.findNavController(this).navigate(action)

                    true
                }
                R.id.about_item -> {
                    val action =
                        TicketQRFragmentDirections.actionTicketQRFragmentToAboutFragment()
                    NavHostFragment.findNavController(this).navigate(action)

                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }


    }

