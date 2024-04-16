import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.asseccoandoid.R
import com.example.asseccoandoid.listeners.PaymentDialogListener
import com.example.asseccoandoid.model.PaymentOrder
import com.example.asseccoandoid.response.PaymentResponse
import com.example.asseccoandoid.service.PaymentService
import com.example.asseccoandoid.singleton.RetrofitClient
import com.example.asseccoandoid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentDialogFragment : DialogFragment() {

    private lateinit var recipientNameEditText: EditText
    private lateinit var senderIbanEditText: EditText
    private lateinit var recipientIbanEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var paymentDescriptionEditText: EditText
    private var listener: PaymentDialogListener? = null


    private var senderIban: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the sender IBAN passed to the fragment
        senderIban = arguments?.getString("sender_iban")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? PaymentDialogListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_payment_form, null)

        // Initialize all EditTexts here after the view has been created
        recipientNameEditText = view.findViewById(R.id.recipient_name_edit_text)
        senderIbanEditText = view.findViewById(R.id.sender_iban_edit_text)
        recipientIbanEditText = view.findViewById(R.id.recipient_iban_edit_text)
        amountEditText = view.findViewById(R.id.amount_edit_text)
        paymentDescriptionEditText = view.findViewById(R.id.payment_description_edit_text)

        // Set the sender IBAN if it's passed as an argument
        senderIbanEditText.setText(senderIban)

        val submitButton: Button = view.findViewById(R.id.submit_payment_button)
        submitButton.setOnClickListener {
            val sessionManager = SessionManager(requireContext())
            val userIdString = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID]

            // Attempt to convert userIdString to Long
            val userId = try {
                Log.e("PaymentDialogFragment", "User ID: $userIdString")
                userIdString?.toLong()
            } catch (e: NumberFormatException) {
                Log.e("PaymentDialogFragment", "Invalid user ID format: $userIdString", e)
                null // Return null if conversion fails
            }

            // Check if userId is null and handle the error appropriately
            if (userId == null) {
                // Handle error: user ID is null or not a valid Long
                Log.e("PaymentDialogFragment", "User ID is null or not a valid Long")
                return@setOnClickListener
            }

            // Proceed with the rest of your code if userId is valid
            val recipientName = recipientNameEditText.text.toString()
            val recipientIban = recipientIbanEditText.text.toString()
            val amount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val paymentDescription = paymentDescriptionEditText.text.toString()

            val paymentOrder = PaymentOrder(
                recipientName = recipientName,
                senderIban = senderIban.toString(),
                recipientIban = recipientIban,
                amount = amount,
                paymentDescription = paymentDescription,
                accountId = userId, // Already a Long
                paymentType = "PAYMENT"
            )

            val paymentService = RetrofitClient.createService(PaymentService::class.java)
            paymentService.createPaymentOrder(paymentOrder).enqueue(object : Callback<PaymentResponse> {
                override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                    if (response.isSuccessful) {
                        // Handle success
                        Log.e("PaymentDialogFragment", "Payment successful")
                        listener?.onPaymentSuccess()
                        dismiss()
                    } else {
                        // Handle error
                        Log.e("PaymentDialogFragment", "Payment failed: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                    // Handle failure
                    Log.e("PaymentDialogFragment", "Error making payment: ${t.message}")
                }
            })
        }

        builder.setView(view)
            .setTitle("Make a Payment")
            .setNegativeButton(android.R.string.cancel) { dialog, id ->
                dialog?.cancel()
            }
        return builder.create()
    }

    companion object {
        fun newInstance(senderIban: String): PaymentDialogFragment {
            val fragment = PaymentDialogFragment()
            val args = Bundle()
            args.putString("sender_iban", senderIban)
            fragment.arguments = args
            return fragment
        }
    }
}
