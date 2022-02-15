package com.example.recipedemoapp.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.recipedemoapp.R
import com.example.recipedemoapp.entities.Category
import java.lang.Exception

class RecipeTypeAdapter(
    private val mContext: Context,
    private val mLayoutResourceId: Int,
    recipeTypes: List<Category>
):
    ArrayAdapter<Category>(mContext, mLayoutResourceId, recipeTypes) {

    var arrList = ArrayList<Category>()
    var ctx: Context? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = (mContext as Activity).layoutInflater
            convertView = inflater.inflate(mLayoutResourceId, parent, false)
        }
        try {
            val category:Category = getItem(position)!!
            val categoryAutoCompleteView = convertView!!.findViewById<View>(R.id.item_dropdownlist) as TextView
            categoryAutoCompleteView.text = category.strcategory
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convertView!!
    }

    override fun getItem(position: Int): Category? {
        return arrList[position]
    }

    override fun getItemId(position: Int): Long {
        return arrList[position].id.toLong()
    }


    override fun getCount(): Int {
        return arrList.size
    }

}