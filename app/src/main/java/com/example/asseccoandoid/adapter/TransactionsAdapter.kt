package com.example.asseccoandoid.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.asseccoandoid.R
import com.example.asseccoandoid.model.Transaction

class TransactionsAdapter(private var transactions: List<Transaction>, private val currentUserAccountId: Long) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    // This class is an inner class of TransactionsAdapter
    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipientNameTextView: TextView = itemView.findViewById(R.id.recipientName)
        private val amountTextView: TextView = itemView.findViewById(R.id.amount)
        private val dateTextView: TextView = itemView.findViewById(R.id.transactionDate)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.paymentDescription)

        fun bind(transaction: Transaction) {
            recipientNameTextView.text = transaction.recipientName ?: "N/A"
            // Log to check the transaction account ID and current user account ID
            Log.e("TransactionBind", "Transaction Account ID: ${transaction}, Current User ID: $currentUserAccountId")
            // Determine the sign based on whether the transaction account ID matches the current user account ID
            val sign = if (transaction.accountId == currentUserAccountId) "-" else "+"
            amountTextView.text = "$sign${transaction.amount}"
            dateTextView.text = transaction.transactionDate
            descriptionTextView.text = transaction.paymentDescription ?: "No description"

            val amountTextColor = if (sign == "-") R.color.red else R.color.green
            amountTextView.setTextColor(ContextCompat.getColor(itemView.context, amountTextColor))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
