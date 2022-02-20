package com.example.recipedemoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.recipedemoapp.adapter.RecipeAdapter
import com.example.recipedemoapp.adapter.RecipeTypeAdapter
import com.example.recipedemoapp.database.RecipeDatabase
import com.example.recipedemoapp.databinding.FragmentHomeBinding
import com.example.recipedemoapp.entities.Category
import com.example.recipedemoapp.entities.Recipes
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment() {
    private var recipeAdapter: RecipeAdapter = RecipeAdapter()
    private var recipeTypeAdapter: RecipeTypeAdapter = RecipeTypeAdapter()
    private var arrRecipe = ArrayList<Recipes>()
    private var arrRecipeType = ArrayList<Category>()
    private var recipeType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            recipeType = requireArguments().getString("recipeType", null)
            recipeTypeAdapter.setOnClickListener(onClicked)

        }
    }

    override fun onResume() {
        super.onResume()
        getRecipeDataFromDb(recipeType)
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

        _binding!!.rvRecipeTypes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        _binding!!.tvRecipeType.text = recipeType

//        _binding!!.recyclerView.setHasFixedSize(true)

//        _binding!!.recyclerView.layoutManager =
//            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        _binding!!.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        launch {
            context?.let {
                var recipeTypes = RecipeDatabase.getDatabase(it).recipeDao().getAllCategory()
                var recipes = RecipeDatabase.getDatabase(it).recipeDao().getSpecificRecipeList(recipeType)

                // get data from db and add it to adapter using setData()
                arrRecipeType = recipeTypes as ArrayList<Category>
                arrRecipeType.reverse()
                recipeTypeAdapter.setData(arrRecipeType)
                _binding!!.rvRecipeTypes.adapter = recipeTypeAdapter
                recipeAdapter!!.setData(recipes)
                _binding!!.recyclerView.adapter = recipeAdapter


                arrRecipe = recipes as ArrayList<Recipes>
            }
        }

        recipeAdapter!!.setOnClickListener(onClickedRecipe)

        _binding!!.fabBtnCreateNote.setOnClickListener {
            replaceFragment(CreateRecipeFragment.newInstance(), false)
        }

        // to make whole search bar clickable even when iconifiedbydefault is true
        _binding!!.searchView.setOnClickListener {
            _binding!!.searchView.onActionViewExpanded()
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

    private fun getRecipeDataFromDb(recipeType: String) {
        _binding!!.tvRecipeType.text = recipeType
        launch {
            context?.let {
                var recipes = RecipeDatabase.getDatabase(it).recipeDao().getSpecificRecipeList(recipeType)

                // get data from db and add it to adapter using setData()
                recipeAdapter!!.setData(recipes)
                _binding!!.recyclerView.adapter = recipeAdapter

                arrRecipe = recipes as ArrayList<Recipes>
            }
        }
    }

    private val onClicked = object : RecipeTypeAdapter.OnItemClickListener{
        override fun onClicked(cat: String) {
            recipeType = cat
            getRecipeDataFromDb(recipeType)
        }
    }

    private val onClickedRecipe = object : RecipeAdapter.OnItemClickListener{
        override fun onClicked(recipeId: Int) {

            val fragment : Fragment
            val bundle = Bundle()
            bundle.putInt("recipeId", recipeId)
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