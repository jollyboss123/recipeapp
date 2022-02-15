package com.example.recipedemoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.notesapp.entities.converters.BitmapConverter
import com.example.notesapp.entities.converters.UriConverter
import com.example.recipedemoapp.dao.RecipeDao
import com.example.recipedemoapp.entities.Category
import com.example.recipedemoapp.entities.Recipes
import com.example.recipedemoapp.entities.converters.RecipeTypeConverter

@Database(entities = [Recipes::class, Category::class], version = 1, exportSchema = false)
@TypeConverters(BitmapConverter::class, UriConverter::class, RecipeTypeConverter::class)
abstract class RecipeDatabase: RoomDatabase() {

    companion object{
        var recipeDatabase: RecipeDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): RecipeDatabase {
            if (recipeDatabase == null) {
                recipeDatabase = Room.databaseBuilder(
                    context,
                    RecipeDatabase::class.java,
                    "notes.db"
                ).build()
            }
            return recipeDatabase!!
        }
    }
    abstract fun recipeDao(): RecipeDao
}