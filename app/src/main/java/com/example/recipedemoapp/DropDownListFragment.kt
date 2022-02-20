package com.example.recipedemoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.recipedemoapp.adapter.RecipeTypeDropdownAdapter
import com.example.recipedemoapp.database.RecipeDatabase
import com.example.recipedemoapp.databinding.FragmentDropdownListBinding
import kotlinx.coroutines.launch

class DropDownListFragment : BaseFragment(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        // to close app on back pressed event
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (shouldInterceptBackPress()) {
                    activity?.finish()
                } else {
                    isEnabled = false
                    activity?.onBackPressed()
                }
            }
        })
    }

    private fun shouldInterceptBackPress(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        launch {
            context?.let {
                var recipeTypes = RecipeDatabase.getDatabase(it).recipeDao().getAllCategory()
                val arrayAdapter = RecipeTypeDropdownAdapter(requireContext(), R.layout.item_dd_recipe_type, recipeTypes)
                binding.autoCompleteTextView.setAdapter(arrayAdapter)
                binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                    val recipes = arrayAdapter.getItem(position)
                    binding.autoCompleteTextView.setText(recipes?.strcategory)
                    var fragment : Fragment
                    var bundle = Bundle()
                    bundle.putString("recipeType", recipes?.strcategory)
                    fragment = HomeFragment.newInstance()
                    fragment.arguments = bundle
                    replaceFragment(fragment, false)
                }
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



