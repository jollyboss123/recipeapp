package com.example.recipedemoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipedemoapp.R
import com.example.recipedemoapp.databinding.ItemRvRecipetypeBinding
import com.example.recipedemoapp.entities.Category
import com.example.recipedemoapp.util.GlideApp

class RecipeTypeAdapter():
    RecyclerView.Adapter<RecipeTypeAdapter.RecipesViewHolder>() {

    var arrList = ArrayList<Category>()
    var listener: OnItemClickListener? = null
    var ctx: Context? = null

    class RecipesViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemRvRecipetypeBinding.bind(view)
    }

    fun setData(arrRecipeList: List<Category>){
        arrList = arrRecipeList as ArrayList<Category>
    }

    fun setOnClickListener(listener1:OnItemClickListener){
        listener = listener1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        ctx = parent.context
        return RecipesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_recipetype, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        with(holder){
            binding.tvRecipeType.text = arrList[position].strcategory

            if (arrList[position].strcategorythumb != ""){
                GlideApp.with(ctx!!).load(arrList[position].strcategorythumb).into(binding.imgDish)
            }
            binding.root.setOnClickListener {
                listener!!.onClicked(arrList[position].strcategory!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    interface OnItemClickListener{
        fun onClicked(cat: String)
    }
}