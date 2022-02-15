package com.example.recipedemoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.module.AppGlideModule
import com.example.recipedemoapp.adapter.RecipeAdapter
import com.example.recipedemoapp.database.RecipeDatabase
import com.example.recipedemoapp.databinding.FragmentHomeBinding
import com.example.recipedemoapp.entities.Recipes
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {
    var recipeAdapter: RecipeAdapter = RecipeAdapter()
    var arrRecipe = ArrayList<Recipes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.recyclerView.setHasFixedSize(true)

        _binding!!.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        launch {
            context?.let {
                var recipes = RecipeDatabase.getDatabase(it).recipeDao().getAllNotes()

                // get data from db and add it to adapter using setData()
                recipeAdapter!!.setData(recipes)
                _binding!!.recyclerView.adapter = recipeAdapter


                arrRecipe = recipes as ArrayList<Recipes>
            }
        }

        recipeAdapter!!.setOnClickListener(onClicked)

        _binding!!.fabBtnCreateNote.setOnClickListener {
            replaceFragment(CreateRecipeFragment.newInstance(), false)
        }

        _binding!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var tempArr = ArrayList<Recipes>()

                for (arr in arrRecipe) {
                    if (arr.recipename!!.toLowerCase(Locale.getDefault())
                            .contains(newText.toString())
                    ) {
                        tempArr.add(arr)
                    }
                }

                recipeAdapter.setData(tempArr)
                recipeAdapter.notifyDataSetChanged()

                return true
            }

        })
    }

    private val onClicked = object : RecipeAdapter.OnItemClickListener{
        override fun onClicked(recipesId: Int) {

            var fragment : Fragment
            var bundle = Bundle()
            bundle.putInt("recipeId", recipesId)
            fragment = CreateRecipeFragment.newInstance()
            fragment.arguments = bundle
            replaceFragment(fragment, false)
        }
    }

    fun replaceFragment(fragment: Fragment, istransition: Boolean){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (istransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }
}