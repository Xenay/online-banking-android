package com.example.asseccoandoid


import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.asseccoandoid.databinding.ActivityMainBinding
import com.example.asseccoandoid.model.Account
import com.example.asseccoandoid.util.SessionManager
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import com.example.asseccoandoid.model.BankAccount
import com.example.asseccoandoid.model.TransferPayload
import com.example.asseccoandoid.response.TransferResponse
import com.example.asseccoandoid.service.BankAccountService
import com.example.asseccoandoid.singleton.RetrofitClient


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)


        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Check if the user is already logged in
        if (!sessionManager.isLoggedIn()) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
            return // Prevent further execution
        }

        // Intent to navigate directly to the Dashboard if logged in
        if (intent.getBooleanExtra("navigateToDashboard", true)) {
            navController.navigate(R.id.dashboardFragment)

        }

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Select an action", Snackbar.LENGTH_LONG)
                .setAction("Options") {
                    showOptionsDialog()
                }.show()
        }


    }

    private fun isUserLoggedIn(): Boolean {
        // Placeholder: Implement actual logic to check if user is logged in
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showOptionsDialog() {
        val options = arrayOf("View Transactions", "Internal Transfer", "Logout")
        AlertDialog.Builder(this)
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> navController.navigate(R.id.transactionsFragment)
                    1 -> showTransferDialog()
                    2 -> logOutUser()
                }
            }
            .show()
    }
    private fun showTransferDialog() {
        val dialogView = layoutInflater.inflate(R.layout.transfer_dialog, null)
        val fromAccountSpinner: Spinner = dialogView.findViewById(R.id.fromAccountSpinner)
        val toAccountSpinner: Spinner = dialogView.findViewById(R.id.toAccountSpinner)
        val amountEditText: EditText = dialogView.findViewById(R.id.transferAmount)
        val descriptionEditText: EditText = dialogView.findViewById(R.id.transferDescription)

        fetchBankAccounts { accounts ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, accounts)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            fromAccountSpinner.adapter = adapter
            // Assuming at least two accounts for toAccount spinner to ensure different account selection
            if (accounts.size > 1) {
                toAccountSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, accounts)
            } else {
                toAccountSpinner.adapter = adapter
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.submitTransfer).setOnClickListener {
            val fromAccount = fromAccountSpinner.selectedItem as BankAccount
            val toAccount = toAccountSpinner.selectedItem as BankAccount
            val amount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val description = descriptionEditText.text.toString()

            if (fromAccount.id != toAccount.id) {
                submitTransfer(fromAccount.id, toAccount.id, amount, description)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Source and destination accounts can't be the same.", Toast.LENGTH_LONG).show()
            }
        }

        dialogView.findViewById<Button>(R.id.cancelTransfer).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun fetchBankAccounts(callback: (List<BankAccount>) -> Unit) {
        val userDetails = sessionManager.getUserDetails()
        val username = userDetails[SessionManager.KEY_USERNAME] ?: return // Safely return if username is null

        val bankAccountService = RetrofitClient.createService(BankAccountService::class.java)
        bankAccountService.getBankAccounts(username).enqueue(object : Callback<List<BankAccount>> {
            override fun onResponse(call: Call<List<BankAccount>>, response: Response<List<BankAccount>>) {
                if (response.isSuccessful) {
                    val accounts = response.body() ?: emptyList()
                    callback(accounts)  // Pass accounts to the callback
                } else {
                    Log.e("DashboardFragment", "Failed to fetch bank accounts: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<BankAccount>>, t: Throwable) {
                Log.e("DashboardFragment", "Error fetching bank accounts: ${t.message}")
            }
        })
    }


    private fun submitTransfer(fromAccountId: Long, toAccountId: Long, amount: Double, description: String) {
        val payload = TransferPayload(fromAccountId, toAccountId, amount, description)
        val service = RetrofitClient.createService(BankAccountService::class.java)
        service.transfer(payload).enqueue(object : Callback<TransferResponse> {
            override fun onResponse(call: Call<TransferResponse>, response: Response<TransferResponse>) {
                if (response.isSuccessful) {
                    Log.d("Transfer", "Transfer successful: ${response.body()?.message}")
                    // Optionally, show a toast or alert dialog here to inform the user of success
                } else {
                    Log.e("Transfer", "Failed to execute transfer: ${response.errorBody()?.string()}")
                    // Optionally, show a toast or alert dialog here to inform the user of failure
                }
            }

            override fun onFailure(call: Call<TransferResponse>, t: Throwable) {
                Log.e("Transfer", "Error during transfer: ${t.message}")
                // Optionally, show a toast or alert dialog here to inform the user of an error
            }
        })
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun logOutUser() {
        // Clear the user session
        sessionManager.logoutUser()

        // Redirect to LoginActivity
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
        finish()  // Close the current activity
    }
}
