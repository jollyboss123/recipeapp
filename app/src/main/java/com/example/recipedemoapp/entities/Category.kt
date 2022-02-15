package com.example.recipedemoapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "RecipeTypes")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @SerializedName("idCategory")
    @Expose
    var idcategory: String,

    @SerializedName("strCategory")
    @Expose
    var strcategory: String,

    @SerializedName("strCategoryThumb")
    @Expose
    var strcategorythumb: String
)