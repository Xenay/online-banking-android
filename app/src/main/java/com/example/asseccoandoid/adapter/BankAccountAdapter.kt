package com.example.asseccoandoid.adapter

import PaymentDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.asseccoandoid.R
import com.example.asseccoandoid.listeners.BankAccountClickListener
import com.example.asseccoandoid.model.BankAccount

class BankAccountAdapter(private var bankAccounts: List<BankAccount>, private val clickListener: BankAccountClickListener)
    : RecyclerView.Adapter<BankAccountAdapter.ViewHolder>() {
    private var isLoaded = false
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeText: TextView = view.findViewById(R.id.typeText)
        val nameText: TextView = view.findViewById(R.id.nameText)
        val balanceText: TextView = view.findViewById(R.id.balanceText)
        val ibanText: TextView = view.findViewById(R.id.ibanText)
        val buttonOverview: Button = view.findViewById(R.id.buttonOverview)
        val buttonTransactions: Button = view.findViewById(R.id.buttonTransactions)
        val buttonPay: Button = view.findViewById(R.id.buttonPay)

        init {
            buttonOverview.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onAccountClick(bankAccounts[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bank_account_item, parent, false)
        return ViewHolder(view) // No need to pass clickListener or bankAccounts since ViewHolder can access them
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("ADAPTER", "Binding ViewHolder at position $position")


            if (position >= 0 && position < bankAccounts.size) {
                val bankAccount = bankAccounts[position]

                holder.typeText.text = bankAccount.type
                holder.nameText.text = bankAccount.name
                holder.balanceText.text = bankAccount.balance.toString()
                holder.ibanText.text = bankAccount.iban

                holder.buttonTransactions.setOnClickListener { v -> }

                holder.buttonPay.setOnClickListener { v ->
                    val context = holder.itemView.context
                    if (context is AppCompatActivity) {
                        // Pass the IBAN of the clicked item to the PaymentDialogFragment
                        val bankAccount = bankAccounts[position]
                        val fm = context.supportFragmentManager
                        val paymentDialogFragment = PaymentDialogFragment.newInstance(bankAccount.iban)
                        paymentDialogFragment.show(fm, "payment_dialog")
                    }
                }


                Log.d("ADAPTER", "Bank account details at position $position: $bankAccount")
            } else {
                Log.e("ADAPTER", "Invalid position: $position")
            }


    }
    fun updateData(newBankAccounts: List<BankAccount>) {
        Log.e("UPDATE DATA", "Updating data with new bank accounts: $newBankAccounts")

            bankAccounts = newBankAccounts
            notifyDataSetChanged() // Notify any registered observers that the data set has changed.


    }

    override fun getItemCount() = bankAccounts.size.also {
        Log.e("ADAPTER", "getItemCount: $it")
    }}

