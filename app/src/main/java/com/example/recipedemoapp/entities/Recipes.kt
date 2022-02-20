package com.example.recipedemoapp.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Recipes")
class Recipes: Serializable{
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null

    @ColumnInfo(name = "recipeType")
    var recipetype: String? = null

    @ColumnInfo(name = "recipeName")
    var recipename: String? = null

    @ColumnInfo(name = "ingredients")
    var ingredients: String? = null

    @ColumnInfo(name = "dateTime")
    var datetime: String? = null

    @ColumnInfo(name = "recipeSteps")
    var recipesteps: String? = null

    @ColumnInfo(name = "imgPath")
    var imgpath: String? = null

    @ColumnInfo(name = "imgUri")
    var imguri: Uri? = null

    @ColumnInfo(name = "webLink")
    var weblink: String? = null

    override fun toString(): String {
        return "$recipename: $datetime"
    }
}

