package com.example.festivalwatch.ticket

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.example.festivalwatch.R
import com.example.festivalwatch.databinding.FragmentProfileBinding
import com.example.festivalwatch.databinding.FragmentTicketBinding
import com.example.festivalwatch.festivalMap.MapViewModelFactory
import com.example.festivalwatch.festivalMenu.FestivalMenuFragmentDirections
import com.example.festivalwatch.home.HomeFragmentArgs
import com.example.festivalwatch.home.HomeViewModelFactory
import com.example.festivalwatch.ticketQR.TicketQRFragmentArgs
import com.example.festivalwatch.ticketQR.TicketQRFragmentDirections
import com.example.festivalwatch.user.ProfileViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.pay.PayClient
import com.google.android.gms.wallet.PaymentData
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONException
import org.json.JSONObject

class TicketFragment : Fragment() {

    private lateinit var viewModel: TicketViewModel
    private lateinit var viewModelFactory: TicketViewModelFactory
    private lateinit var googlePayButton: View
    private lateinit var imagePay: ImageView
    private lateinit var textPrice: TextView
    private lateinit var textInformation: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TicketFragment", "TicketFragment created/re-created!")
        // Inflate the layout for this fragment

        viewModelFactory = TicketViewModelFactory(
            requireActivity().application,
            TicketFragmentArgs.fromBundle(requireArguments()).token!!,
            TicketFragmentArgs.fromBundle(requireArguments()).username!!,
            TicketFragmentArgs.fromBundle(requireArguments()).title!!,
            TicketFragmentArgs.fromBundle(requireArguments()).date!!,
            TicketFragmentArgs.fromBundle(requireArguments()).time!!,
            TicketFragmentArgs.fromBundle(requireArguments()).price!!,
            TicketFragmentArgs.fromBundle(requireArguments()).photoDesc!!)
        Log.d("TicketFragment", "TicketFragment binding created/re-created!")

        viewModel = ViewModelProvider(this, viewModelFactory).get(TicketViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentTicketBinding>(inflater,
            R.layout.fragment_ticket, container, false)

        binding.ticketViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.canUseGooglePay.observe(viewLifecycleOwner, Observer(::setGooglePayAvailable))


        googlePayButton = binding.root.findViewById(R.id.googlePayButton)

        Glide.with(this).load(viewModel.photoDesc.value).into(binding.imageViewFestivalId)

        googlePayButton.setOnClickListener {
            requestPayment()
        }


        setHasOptionsMenu(true)

        return binding.root
    }

    /**
     * If isReadyToPay returned `true`, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns `false`.
     *
     * @param available isReadyToPay API response.
     */
    private fun setGooglePayAvailable(available: Boolean) {
        if (available) {
            googlePayButton.visibility = View.VISIBLE
        } else {
            Toast.makeText(
                requireActivity(),
                R.string.google_pay_status_unavailable,
                Toast.LENGTH_LONG).show()
        }
    }


    private fun requestPayment() {

        // Disables the button to prevent multiple clicks.
        googlePayButton.isClickable = false

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        val dummyPriceCents = 100L
        val shippingCostCents = 900L
        val task = viewModel.getLoadPaymentDataTask(dummyPriceCents + shippingCostCents)

        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result.let(::handlePaymentSuccess)
            } else {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }
                    is ApiException -> {
                        handleError(exception.statusCode, exception.message)
                    }
                    else -> {
                        handleError(
                            CommonStatusCodes.INTERNAL_ERROR, "Unexpected non API" +
                                    " exception when trying to deliver the task result to an activity!"
                        )
                    }
                }
            }

            // Re-enables the Google Pay payment button.
            googlePayButton.isClickable = true
        }
    }

    // Handle potential conflict from calling loadPaymentData
    private val resolvePaymentForResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            result: ActivityResult ->
        when (result.resultCode) {
            RESULT_OK ->
                result.data?.let { intent ->
                    PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                }

            RESULT_CANCELED -> {
                // The user cancelled the payment attempt
            }
        }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see [Payment
     * Data](https://developers.google.com/pay/api/android/reference/object.PaymentData)
     */
    private fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson()

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("TicketFragment", "BillingName " + billingName)

            Toast.makeText(requireContext(), getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show()

            // Logging token string
            val tokenPayment = paymentMethodData
                .getJSONObject("tokenizationData")
                .getString("token")

            viewModel.qr_code = viewModel.token + viewModel.title.value
            Log.d("TicketFragment", "QR code " + viewModel.qr_code)
            viewModel.postData(viewModel.qr_code)

            val action =
                TicketFragmentDirections.actionTicketFragmentToTicketQRFragment(
                    viewModel.token,
                    viewModel.username,
                    viewModel.title.value!!,
                    viewModel.date.value!!,
                    viewModel.time.value!!,
                    viewModel.priceVal,
                    viewModel.qr_code,
                    viewModel.photoDesc.value!!
                )
            NavHostFragment.findNavController(this).navigate(action)


        } catch (error: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $error")
        }
    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     * WalletConstants.ERROR_CODE_* constants.
     * @see [
     * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
     */
    private fun handleError(statusCode: Int, message: String?) {
        Log.e("Google Pay API error", "Error code: $statusCode, Message: $message")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout_item -> {
                val action =
                    TicketFragmentDirections.actionTicketFragmentToLoginFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            R.id.about_item -> {
                val action =
                    TicketFragmentDirections.actionTicketFragmentToAboutFragment()
                NavHostFragment.findNavController(this).navigate(action)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}