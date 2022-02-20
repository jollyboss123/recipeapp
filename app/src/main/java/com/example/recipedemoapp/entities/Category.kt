package com.example.recipedemoapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "RecipeTypes")
class Category: Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

//    @SerializedName("idCategory")
//    @Expose
//    var idcategory: String? = null

    @SerializedName("strCategory")
    @Expose
    var strcategory: String? = null

    @SerializedName("strCategoryThumb")
    @Expose
    var strcategorythumb: String? = null
}
