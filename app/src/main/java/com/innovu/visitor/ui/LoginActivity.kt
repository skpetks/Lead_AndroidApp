package com.innovu.visitor.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.http.HttpException
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.gson.Gson
import com.innovu.visitor.MainActivity
import com.innovu.visitor.data.api.RetrofitClient
import com.innovu.visitor.databinding.ActivityLoginBinding
import com.innovu.visitor.model.ErrorResponse
import com.innovu.visitor.model.LoginRequest
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val scope = MainScope()

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getSupportActionBar()!!.hide();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding.bntLogin.setOnClickListener {
            scope.launch {
                doLogin()
            }
        }
        binding.btnGetstarted.setOnClickListener {
            binding.llayout1.visibility= View.INVISIBLE
            binding.llayout2.visibility= View.VISIBLE
        }
        binding.edUsername.setText("teacher1")
        binding.edPassword.setText("Test@123")
//         binding.edUsername.setText("security2")
//        binding.edPassword.setText("Test@123")

//        binding.edUsername.setText("Sadmin")
//        binding.edPassword.setText("Admin@123")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        if (StorePrefData.isLogin){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        val androidId = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        StorePrefData.UID=androidId;
    }


    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private suspend fun doLogin() {
        try{
            val quotesApi = RetrofitClient.instance
            val enteredEmail = binding.edUsername.text.toString()
            val enteredPassword = binding.edPassword.text.toString()
            if (enteredEmail.length > 3 && enteredPassword.length > 3) {
                binding.progress.progressBar.isVisible =true
                val res = quotesApi.userLogin(LoginRequest(enteredEmail, enteredPassword, enteredEmail))
                if (res.isSuccessful && res.code() == 200 ) {
                    binding.progress.progressBar.isVisible =false
                    StorePrefData.UserName = res.body()?.user?.UserName.toString();
                    StorePrefData.email = res.body()?.user?.Email.toString();
                    StorePrefData.UserID = res.body()?.user?.UserID.toString();
                    StorePrefData.Role = res.body()?.user?.Role.toString();
                    StorePrefData.RoleId = res.body()?.user?.RoleId!!;
                    StorePrefData.isLogin=true;
                    StorePrefData.OrgId = res.body()?.user?.OrganizationID!!;
                    StorePrefData.UserIId = res.body()?.user?.UserID!!;
                    StorePrefData.BranchId = res.body()?.user?.BranchID!!;
                    StorePrefData.GateID = res.body()?.user?.GateID!!;
                    StorePrefData.DepartmentID = res.body()?.user?.DepartmentID!!;

//                if(StorePrefData.Role.lowercase().contains("superadmin"))
//                {
                    StorePrefData.ParameterUserID = res.body()?.user?.UserID.toString();
//                }
                    if(StorePrefData.Role.lowercase().contains("superadmin"))
                    {
                        StorePrefData.isAdminUserID = "0";
                    }else{
                        StorePrefData.isAdminUserID = "1";
                    }

               //     Toast.makeText(applicationContext,    "Login Sucess..", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
//                val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                startActivity(intent)
                }

                else {
                    binding.progress.progressBar.isVisible =false
//                    val errorBody = res.errorBody()
//                    errorBody?.let {
//                        val gson = Gson()
//                        try {
//                            val error = gson.fromJson(it.charStream(), ErrorResponse::class.java)
//                            Log.e("LOGIN", "Error: ${error.error}")
//                            Log.e("LOGIN", "Details: ${error.details}")
//                        } catch (e: Exception) {
//                            Log.e("LOGIN", "Parsing error response failed", e)
//                        }
//                    }
                    Toast.makeText(
                        applicationContext, "Please enter username & password.", Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                binding.progress.progressBar.isVisible =false
                Toast.makeText(
                    applicationContext, "Please enter username & password.", Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: SocketTimeoutException) {
            // Handle SocketTimeoutException (request took too long)
            Log.e("NetworkError", "Timeout occurred: ${e.message}")
            // You can retry the request or show a timeout message to the user
        } catch (e: HttpException) {
            // Handle other HTTP exceptions
            Log.e("NetworkError", "HTTP error: ${e.message}")
        } catch (e: Exception) {
            // Handle other generic exceptions
            Log.e("NetworkError", "Unexpected error: ${e.message}")
        }
    }
}