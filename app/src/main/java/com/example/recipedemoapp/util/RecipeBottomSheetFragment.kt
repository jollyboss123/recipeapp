package com.example.recipedemoapp.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.recipedemoapp.R
import com.example.recipedemoapp.databinding.FragmentRecipeBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecipeBottomSheetFragment : BottomSheetDialogFragment() {

    companion object{
        var recipeId = -1
        fun newInstance(id: Int): RecipeBottomSheetFragment{
            val args = Bundle()
            val fragment = RecipeBottomSheetFragment()
            fragment.arguments = args
            recipeId = id
            return fragment
        }
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val view = LayoutInflater.from(context).inflate(R.layout.fragment_recipe_bottom_sheet, null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams

        val behavior = param.behavior

        if (behavior is BottomSheetBehavior<*>){
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    TODO("Not yet implemented")
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    var state = ""
                    when (newState) {
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            state = "DRAGGING"
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            state = "SETTLING"
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            state = "EXPANDED"
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            state = "COLLAPSED"
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            state = "HIDDEN"
                            dismiss()
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
            })
        }

    }

    private var _binding: FragmentRecipeBottomSheetBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecipeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (recipeId != -1) {
            _binding!!.layoutDeleteRecipe.visibility = View.VISIBLE
            _binding!!.layoutUpdateRecipe.visibility = View.VISIBLE
            _binding!!.layoutImage.visibility = View.GONE
        } else {
            _binding!!.layoutDeleteRecipe.visibility = View.GONE
            _binding!!.layoutUpdateRecipe.visibility = View.GONE
            _binding!!.layoutImage.visibility = View.VISIBLE
        }
        setListener()
    }

    private fun setListener(){

        _binding!!.layoutImage.setOnClickListener{
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Image")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }

        _binding!!.layoutUpdateRecipe.setOnClickListener{
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "UpdateRecipe")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }

        _binding!!.layoutDeleteRecipe.setOnClickListener{
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "DeleteRecipe")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
    }
}