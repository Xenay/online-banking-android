package com.example.asseccoandoid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asseccoandoid.adapter.TransactionsAdapter
import com.example.asseccoandoid.databinding.FragmentTransactionsBinding
import com.example.asseccoandoid.model.Transaction
import com.example.asseccoandoid.service.BankApiService
import com.example.asseccoandoid.singleton.RetrofitClient
import com.example.asseccoandoid.util.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionsFragment : Fragment() {
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    // Assuming you have a list of transactions to display
    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionManager = SessionManager(requireContext())
        val currentUserAccountId = sessionManager.getUserDetails()[SessionManager.KEY_USER_ID]
            ?: null
        if (currentUserAccountId != null) {
            setupRecyclerView(currentUserAccountId.toLong())
        }
        if (currentUserAccountId != null) {
            loadTransactions(currentUserAccountId.toLong())
        }
    }

    private fun setupRecyclerView(currentUserAccountId: Long) {
        transactionsAdapter = TransactionsAdapter(emptyList(), currentUserAccountId)
        binding.transactionsRecyclerView.adapter = transactionsAdapter
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(context)
    }



    private fun loadTransactions(currentUserAccountId: Long) {
        val service = RetrofitClient.createService(BankApiService::class.java)
        service.getUserTransactions(currentUserAccountId).enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    transactionsAdapter.updateData(response.body() ?: emptyList())
                } else {
                    Log.e("TransactionFetch", "Response not successful: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Log.e("TransactionFetch", "Failed to fetch transactions", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

