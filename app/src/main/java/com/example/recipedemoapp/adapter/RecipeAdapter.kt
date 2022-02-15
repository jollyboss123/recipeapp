package com.example.recipedemoapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipedemoapp.R
import com.example.recipedemoapp.databinding.ItemRvRecipeBinding
import com.example.recipedemoapp.entities.Recipes
import com.example.recipedemoapp.util.GlideApp
import com.example.recipedemoapp.util.GlideModule

class RecipeAdapter():
    RecyclerView.Adapter<RecipeAdapter.RecipesViewHolder>() {

    var arrList = ArrayList<Recipes>()
    var listener: OnItemClickListener? = null
    var ctx: Context? = null

    class RecipesViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemRvRecipeBinding.bind(view)
    }

    fun setData(arrRecipeList: List<Recipes>){
        arrList = arrRecipeList as ArrayList<Recipes>
    }

    fun setOnClickListener(listener1:OnItemClickListener){
        listener = listener1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        ctx = parent.context
        return RecipesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_recipe, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        with(holder){
            binding.tvTitle.text = arrList[position].recipename
            binding.tvDesc.text = arrList[position].recipesteps
            binding.tvDateTime.text = arrList[position].datetime

//            if (arrList[position].color != null) {
//                binding.cardView.setCardBackgroundColor(Color.parseColor(arrList[position].color))
//            } else {
//                binding.cardView.setCardBackgroundColor(Color.parseColor(R.color.lightBlack.toString()))
//            }

            if (arrList[position].imgpath != ""){
//                var bitmap = BitmapFactory.decodeFile(arrList[position].imgpath)
//                binding.imgNote.setImageBitmap(bitmap)
                GlideApp.with(ctx!!).load(arrList[position].imgpath).into(binding.imgRecipe)
                binding.imgRecipe.visibility = View.VISIBLE
            } else {
                binding.imgRecipe.visibility = View.GONE
            }

            if (arrList[position].weblink != ""){
                binding.tvWebLink.text = arrList[position].weblink
                binding.tvWebLink.visibility = View.VISIBLE
            } else {
                binding.tvWebLink.visibility = View.GONE
            }

            binding.cardView.setOnClickListener {
                listener!!.onClicked(arrList[position].id!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    interface OnItemClickListener{
        fun onClicked(noteId:Int)
    }
}