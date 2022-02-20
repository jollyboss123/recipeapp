package com.example.recipedemoapp.dao

import androidx.room.*
import com.example.recipedemoapp.entities.Category
import com.example.recipedemoapp.entities.Recipes

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipetypes ORDER BY id DESC")
    suspend fun getAllCategory(): List<Category>

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    suspend fun getAllRecipes(): List<Recipes>

    @Query("DELETE FROM recipetypes")
    suspend fun clearDb()

    @Query("SELECT * from recipes WHERE recipeType = :recipeType ORDER BY id DESC")
    suspend fun getSpecificRecipeList(recipeType: String): List<Recipes>

    @Query("SELECT strcategory from recipetypes ORDER BY id DESC")
    suspend fun getAllCategoryName(): List<String>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getSpecificRecipe(id:Int): Recipes

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipe: Recipes)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteSpecificRecipe(id:Int)

    @Update
    suspend fun updateRecipe(recipes:Recipes)
}