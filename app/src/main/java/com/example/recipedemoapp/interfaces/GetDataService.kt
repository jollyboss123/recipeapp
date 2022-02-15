package com.example.recipedemoapp.interfaces

import com.example.recipedemoapp.entities.Category
import retrofit2.Call
import retrofit2.http.GET

interface GetDataService {
    @GET("categories.php")
    fun getCategoryList(): Call<Category>
}