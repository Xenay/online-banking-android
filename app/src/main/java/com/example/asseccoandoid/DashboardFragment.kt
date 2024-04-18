package com.example.asseccoandoid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.asseccoandoid.adapter.BankAccountAdapter
import com.example.asseccoandoid.listeners.PaymentDialogListener
import com.example.asseccoandoid.model.BankAccount
import com.example.asseccoandoid.service.BankAccountService
import com.example.asseccoandoid.singleton.RetrofitClient
import com.example.asseccoandoid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment(), PaymentDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BankAccountAdapter;
    private lateinit var sessionManager: SessionManager



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        sessionManager = SessionManager(requireContext())
        initializeRecyclerView(view)
        fetchBankAccounts()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            fetchBankAccounts()
        }
    }
    override fun onPaymentSuccess() {
        fetchBankAccounts()
    }

    private fun initializeRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.bankAccountsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = BankAccountAdapter(emptyList()) // Initialize with an empty list
        recyclerView.adapter = adapter
    }

    private fun fetchBankAccounts() {
        val userDetails = sessionManager.getUserDetails()
        val username = userDetails[SessionManager.KEY_USERNAME] ?: return // Safely return if username is null

        val bankAccountService = RetrofitClient.createService(BankAccountService::class.java)
        bankAccountService.getBankAccounts(username).enqueue(object : Callback<List<BankAccount>> {
            override fun onResponse(call: Call<List<BankAccount>>, response: Response<List<BankAccount>>) {
                if (response.isSuccessful) {
                    val bankAccounts = response.body() ?: emptyList()

                    Log.e("IMPORTANT", "Accounts: $bankAccounts")

                    adapter?.updateData(bankAccounts)
                } else {
                    Log.e("DashboardFragment", "Failed to fetch bank accounts: ${response.code()}")
                }


            }
            operator fun invoke(call: Call<List<BankAccount>>, response: Response<List<BankAccount>>?) {
                if (response != null) {
                    onResponse(call, response)
                }
                // Cancel the Call object after use
                call.cancel()
            }


            override fun onFailure(call: Call<List<BankAccount>>, t: Throwable) {
                Log.e("DashboardFragment", "Error fetching bank accounts: ${t.message}")
            }



        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                // Add any setup code here if needed
            }
    }
}
