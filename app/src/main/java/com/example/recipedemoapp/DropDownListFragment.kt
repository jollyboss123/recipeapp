package com.example.recipedemoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.recipedemoapp.adapter.RecipeAdapter
import com.example.recipedemoapp.adapter.RecipeTypeAdapter
import com.example.recipedemoapp.database.RecipeDatabase
import com.example.recipedemoapp.databinding.FragmentDropdownListBinding
import com.example.recipedemoapp.entities.Category
import kotlinx.coroutines.launch

class DropDownListFragment : BaseFragment(){

    var arrCategory = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onResume() {
        super.onResume()
        launch {
            context?.let {
                var recipeTypes = RecipeDatabase.getDatabase(it).recipeDao().getAllCategory()
                for (arr in recipeTypes) {
                    arrCategory.add(arr.strcategory)
                }

                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_dd_recipe_type, arrCategory)
                //var arrayAdapter = RecipeTypeAdapter(requireContext(),R.layout.item_dd_recipe_type, recipeTypes)
                binding.autoCompleteTextView.setAdapter(arrayAdapter)
                binding.autoCompleteTextView.setOnItemClickListener()

            }
        }
    }

    private var _binding: FragmentDropdownListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDropdownListBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DropDownListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        }


    fun replaceFragment(fragment: Fragment, istransition: Boolean){
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (istransition){
            fragmentTransition.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }
        fragmentTransition.replace(R.id.frame_layout, fragment).addToBackStack(fragment.javaClass.simpleName).commit()
    }

    }



