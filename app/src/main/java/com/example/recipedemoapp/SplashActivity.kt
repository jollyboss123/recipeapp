package com.example.recipedemoapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recipedemoapp.database.RecipeDatabase
import com.example.recipedemoapp.databinding.ActivitySplashBinding
import com.example.recipedemoapp.entities.Category
import com.example.recipedemoapp.interfaces.GetDataService
import com.example.recipedemoapp.retrofitclient.RetrofitClientInstance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(R.layout.activity_splash)

        clearDatabase()
        insertDataIntoRoomDb(getRecipeTypes(applicationContext))

        withHandlerRunnable(3_000L)

    }

    private fun launchPostSplashActitivty(){
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun withHandlerRunnable(delay: Long) {
        val runnable = Runnable { launchPostSplashActitivty() }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, delay)
    }

    private fun getRecipeTypes(context: Context): List<Category> {
        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("recipeType.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            Log.d(ioException.toString(), "cannot fetch json")
        }

        val type = object : TypeToken<List<Category>>() {

        }.type

        return Gson().fromJson(jsonString, type)

    }

    private fun insertDataIntoRoomDb(category: List<Category>?){

        launch {
            this.let {

                for (arr in category!!) {
                    RecipeDatabase.getDatabase(this@SplashActivity)
                        .recipeDao().insertCategory(arr)
                }
            }
        }
    }

    fun clearDatabase() {
        launch {
            this.let {
                RecipeDatabase.getDatabase(this@SplashActivity).recipeDao().clearDb()
            }
        }
    }

}