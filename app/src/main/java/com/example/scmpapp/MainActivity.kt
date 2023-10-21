package com.example.scmpapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password should be 6-10 characters, letters and numbers only", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val dialog = AlertDialog.Builder(this@MainActivity)
                .setMessage("Logging in...")
                .setCancelable(false)
                .create()
            dialog.show()

            val result = makeLoginRequest(email, password)
            dialog.dismiss()

            if (result != null && result.isNotBlank()) {
                Toast.makeText(this@MainActivity, "Logged in successfully! Token: $result", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, StaffListActivity::class.java)
                intent.putExtra("TOKEN", result)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@MainActivity, "Login failed!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = "^[a-zA-Z0-9]{6,10}$".toRegex()
        return regex.matches(password)
    }

    private suspend fun makeLoginRequest(email: String, password: String): String? {
        val url = "https://reqres.in/api/login?delay=5"
        val json = JSONObject().put("email", email).put("password", password).toString()

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val token = JSONObject(responseBody).optString("token")
                token
            } catch (e: Exception) {
                null
            }
        }
    }
}

