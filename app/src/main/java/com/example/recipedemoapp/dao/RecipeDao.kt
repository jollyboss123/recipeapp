package com.example.recipedemoapp.dao

import androidx.room.*
import com.example.recipedemoapp.entities.Category
import com.example.recipedemoapp.entities.Recipes

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipetypes ORDER BY id DESC")
    suspend fun getAllCategory(): List<Category>

    @Query("SELECT * FROM notes ORDER BY id DESC")
    suspend fun getAllNotes(): List<Recipes>

    @Query("DELETE FROM recipetypes")
    suspend fun clearDb()

    @Query("SELECT * from notes WHERE recipeType = :recipeType ORDER BY id DESC")
    suspend fun getSpecificRecipeList(recipeType: String): List<Recipes>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getSpecificRecipe(id:Int): Recipes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipe: Recipes)

    @Delete
    suspend fun deleteRecipe(recipes:Recipes)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteSpecificRecipe(id:Int)

    @Update
    suspend fun updateRecipe(recipes:Recipes)
}