package com.example.scmpapp

import EmployeeAdapter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scmpapp.data.model.Employee
import com.example.scmpapp.data.model.EmployeeResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StaffListActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private var currentPage = 1
    private val employees = mutableListOf<Employee>()
    private lateinit var rvStaffList: RecyclerView
    private lateinit var btnLoadMore: Button
    private  lateinit var tvToken: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_list)
        rvStaffList = findViewById(R.id.rvStaffList)
        btnLoadMore = findViewById<Button>(R.id.btnLoadMore)
        tvToken = findViewById(R.id.tvToken)

        val token = intent.getStringExtra("TOKEN")
        tvToken.text = "Token: $token"

        rvStaffList.layoutManager = LinearLayoutManager(this)
        rvStaffList.adapter = EmployeeAdapter(employees)

        loadEmployees()
    }

    private fun loadEmployees() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = Request.Builder()
                .url("https://reqres.in/api/users?page=$currentPage")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    val employeeResponse = Gson().fromJson(jsonResponse, EmployeeResponse::class.java)
                    employees.addAll(employeeResponse.data)

                    withContext(Dispatchers.Main) {
                        rvStaffList.adapter?.notifyDataSetChanged()

                        if (currentPage < employeeResponse.total_pages) {
                            btnLoadMore.visibility = View.VISIBLE
                            btnLoadMore.setOnClickListener {
                                currentPage++
                                loadEmployees()
                            }
                        } else {
                            btnLoadMore.visibility = View.GONE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StaffListActivity, "Error loading data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
