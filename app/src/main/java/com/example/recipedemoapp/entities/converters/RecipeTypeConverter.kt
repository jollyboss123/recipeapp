package com.example.recipedemoapp.entities.converters

import androidx.room.TypeConverter
import com.example.recipedemoapp.entities.Category
import com.example.recipedemoapp.entities.Recipes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipeTypeConverter {

    @TypeConverter fun fromRecipeList(category: List<Category>): String?{
        return if (category == null){
            null
        } else {
            val gson = Gson()
            val type = object : TypeToken<Recipes>(){

            }.type
            gson.toJson(category, type)
        }
    }

    @TypeConverter fun toRecipeList(categoryString: String): List<Category>? {
        if (categoryString == null){
            return null
        } else {
            val gson = Gson()
            val type = object : TypeToken<Category>(){

            }.type
            return gson.fromJson(categoryString, type)
        }
    }
}