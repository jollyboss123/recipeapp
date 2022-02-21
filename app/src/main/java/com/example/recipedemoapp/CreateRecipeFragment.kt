package com.example.recipedemoapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.recipedemoapp.adapter.RecipeTypeDropdownAdapter
import com.example.recipedemoapp.database.RecipeDatabase
import com.example.recipedemoapp.databinding.FragmentCreateRecipeBinding
import com.example.recipedemoapp.databinding.FragmentRecipeBottomSheetBinding
import com.example.recipedemoapp.entities.Category
import com.example.recipedemoapp.entities.Recipes
import com.example.recipedemoapp.util.GlideApp
import com.example.recipedemoapp.util.RecipeBottomSheetFragment
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception

class CreateRecipeFragment : BaseFragment(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

    private var READ_STORAGE_PERM = 123
    private var selectedImagePath = ""
    private var recipeId = -1
    private var selectedImageUri: Uri? = null
    private var recipeTypesList: List<String>? = null
    private var noteBottomSheetFragment: RecipeBottomSheetFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeId = requireArguments().getInt("recipeId", -1)
    }

    private var _binding: FragmentCreateRecipeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)
        return binding.root

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateRecipeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // in the instance of viewing recipes created
        if (recipeId != -1){
            launch {
                context?.let {
                    var recipes = RecipeDatabase.getDatabase(it).recipeDao().getSpecificRecipe(recipeId)
                    _binding!!.tvRecipeName.setText(recipes.recipename)
                    _binding!!.tvIngredients.setText(recipes.ingredients)
                    _binding!!.tvInstructions.setText(recipes.recipesteps)
                    _binding!!.recipeType.setText(recipes.recipetype)
                    // to disable edit text function
                    _binding!!.tvRecipeName.isEnabled = false
                    _binding!!.tvIngredients.isEnabled = false
                    _binding!!.tvInstructions.isEnabled = false
                    _binding!!.recipeType.isEnabled = false
                    // hide save button if view saved recipe
                    _binding!!.imgToolbarBtnSave.visibility = View.GONE
                    // to disable edit img function
                    _binding!!.imgItem.isEnabled = false
                    if (recipes.imgpath != ""){
                        selectedImagePath = recipes.imgpath!!
                        GlideApp.with(requireContext()).load(recipes.imgpath).into(_binding!!.imgItem)
                    }
                }
            }
        }

        // suggest existing recipe types when user input recipe type in creating new recipe
        launch {
            context?.let {
                var recipeTypes = RecipeDatabase.getDatabase(it).recipeDao().getAllCategory()
                recipeTypesList = RecipeDatabase.getDatabase(it).recipeDao().getAllCategoryName()
                val arrayAdapter = RecipeTypeDropdownAdapter(requireContext(), R.layout.item_dd_recipe_type2, recipeTypes)
                _binding!!.recipeType.setAdapter(arrayAdapter)
                // min number of char to type to show the drop down
                _binding!!.recipeType.threshold = 1
                _binding!!.recipeType.setOnItemClickListener { _, _, position, _ ->
                    val recipes = arrayAdapter.getItem(position)
                    _binding!!.recipeType.setText(recipes?.strcategory)
                }
//                _binding!!.recipeType.setOnFocusChangeListener { _, hasFocus ->
//                    if (hasFocus) {
//                        _binding!!.recipeType.showDropDown()
//                    }
//                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            BroadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        _binding!!.imgToolbarBtnSave.setOnClickListener {
            if (recipeId != -1){
                updateRecipe()
            } else {
                saveRecipe()
            }
        }

        _binding!!.imgToolbarBtnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        _binding!!.bottomFragment.setOnClickListener {
            noteBottomSheetFragment = RecipeBottomSheetFragment.newInstance(recipeId)
            noteBottomSheetFragment!!.show(requireActivity().supportFragmentManager, "Note Bottom Sheet Fragment")
        }

        _binding!!.imgItem.setOnClickListener {
            readStorageTaskAndPickImage()
        }

    }
    private fun updateRecipe() {

        launch {
            context?.let {
                var recipe = RecipeDatabase.getDatabase(it).recipeDao().getSpecificRecipe(recipeId)
                recipe.recipename = _binding!!.tvRecipeName.text.toString()
                recipe.ingredients = _binding!!.tvIngredients.text.toString()
                recipe.recipesteps = _binding!!.tvInstructions.text.toString()
                recipe.recipetype = _binding!!.recipeType.text.toString()
                recipe.imgpath = selectedImagePath
                recipe.imguri = selectedImageUri

                RecipeDatabase.getDatabase(it).recipeDao().updateRecipe(recipe)
                _binding!!.tvRecipeName.setText("")
                _binding!!.tvIngredients.setText("")
                _binding!!.tvInstructions.setText("")
                _binding!!.recipeType.setText("")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun saveRecipe() {

        when {
            _binding!!.tvRecipeName.text.isNullOrEmpty() -> {
                Toast.makeText(context, "Recipe name is required", Toast.LENGTH_SHORT).show()
            }
            _binding!!.tvIngredients.text.isNullOrEmpty() -> {
                Toast.makeText(context, "Ingredients is required", Toast.LENGTH_SHORT).show()
            }
            _binding!!.tvInstructions.text.isNullOrEmpty() -> {
                Toast.makeText(context, "Instructions is required", Toast.LENGTH_SHORT).show()
            }
            _binding!!.recipeType.text.isNullOrEmpty() -> {
                Toast.makeText(context, "Recipe type is required", Toast.LENGTH_SHORT).show()
            }
            // create new recipe type
            _binding!!.recipeType.text.toString() !in recipeTypesList!! -> {
                val builder = AlertDialog.Builder(requireContext())
                var newRecipeType = _binding!!.recipeType.text.toString()
                val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                    insertNewRecipeTypeIntoRoomDb(newRecipeType)
                    Toast.makeText(requireContext(), "New recipe type: ${newRecipeType.capitalize()} created",
                        Toast.LENGTH_SHORT).show()
                    saveRecipeAfterCreateNewRecipeType()
                }
                val negativeButtonClick = { dialog: DialogInterface, which: Int ->
                    dialog.cancel()
                }

                with (builder) {
                    setTitle("Androidly Alert")
                    setMessage("Create new recipe type?")
                    setPositiveButton("OK", positiveButtonClick)
                    setNegativeButton("CANCEL", negativeButtonClick)
                    show()
                }
            }
            else -> {

                launch {
                    var recipe = Recipes()
                    recipe.recipename = _binding!!.tvRecipeName.text.toString()
                    recipe.ingredients = _binding!!.tvIngredients.text.toString()
                    recipe.recipesteps = _binding!!.tvInstructions.text.toString()
                    recipe.recipetype = _binding!!.recipeType.text.toString()
                    recipe.imgpath = selectedImagePath
                    recipe.imguri = selectedImageUri
                    context?.let {
                        RecipeDatabase.getDatabase(it).recipeDao().insertRecipes(recipe)
                        _binding!!.tvRecipeName.setText("")
                        _binding!!.tvIngredients.setText("")
                        _binding!!.tvInstructions.setText("")
                        _binding!!.recipeType.setText("")
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }
        }
    }

    private fun saveRecipeAfterCreateNewRecipeType() {
        launch {
            var recipe = Recipes()
            recipe.recipename = _binding!!.tvRecipeName.text.toString()
            recipe.ingredients = _binding!!.tvIngredients.text.toString()
            recipe.recipesteps = _binding!!.tvInstructions.text.toString()
            recipe.recipetype = _binding!!.recipeType.text.toString()
            recipe.imgpath = selectedImagePath
            recipe.imguri = selectedImageUri
            context?.let {
                RecipeDatabase.getDatabase(it).recipeDao().insertRecipes(recipe)
                _binding!!.tvRecipeName.setText("")
                _binding!!.tvIngredients.setText("")
                _binding!!.tvInstructions.setText("")
                _binding!!.recipeType.setText("")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun insertNewRecipeTypeIntoRoomDb(recipeType: String?) {
        val category = Category()
        category.strcategory = recipeType!!.capitalize()
//        if (recipeImg != null || recipeImg != "") {
//            category.strcategorythumb = recipeImg
//        }
        category.strcategorythumb = ""
        launch {
            context?.let {
                RecipeDatabase.getDatabase(it).recipeDao().insertCategory(category)
            }
        }
    }

    private fun deleteRecipe() {

        launch {
            context?.let {
                RecipeDatabase.getDatabase(it).recipeDao().deleteSpecificRecipe(recipeId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private val BroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var action = intent!!.getStringExtra("action")

            when (action!!) {
                "Image" -> {
                    readStorageTaskAndPickImage()
                }

                "UpdateRecipe" -> {
                    _binding!!.tvRecipeName.isEnabled = true
                    _binding!!.tvIngredients.isEnabled = true
                    _binding!!.tvInstructions.isEnabled = true
                    _binding!!.recipeType.isEnabled = true
                    _binding!!.imgToolbarBtnSave.visibility = View.VISIBLE
                    _binding!!.imgItem.isEnabled = true
                }

                "DeleteRecipe" -> {
                    deleteRecipe()
                }

                "CloseBottomFragment" -> {
                    requireFragmentManager().beginTransaction().hide(noteBottomSheetFragment!!)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(BroadcastReceiver)
    }

    private fun hasReadStoragePerm(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun readStorageTaskAndPickImage() {
        if (hasReadStoragePerm()) {
            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                "This app needs access to your storage",
                READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun pickImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            resultLauncher.launch(intent)
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null) {
                    try {
                        GlideApp.with(requireContext()).load(selectedImageUrl).into(_binding!!.imgItem)
                        selectedImagePath = getPathFromUri(selectedImageUrl)!!
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, requireActivity())
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }


}

























