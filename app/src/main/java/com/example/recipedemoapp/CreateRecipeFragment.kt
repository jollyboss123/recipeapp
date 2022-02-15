package com.example.recipedemoapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.recipedemoapp.database.RecipeDatabase
import com.example.recipedemoapp.databinding.FragmentCreateRecipeBinding
import com.example.recipedemoapp.entities.Recipes
import com.example.recipedemoapp.util.GlideApp
import com.example.recipedemoapp.util.RecipeBottomSheetFragment
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class CreateRecipeFragment : BaseFragment(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{
    var selectedColor = "#FF171C26"
    var currentDate:String? = null
    private var READ_STORAGE_PERM = 123
    private var selectedImagePath = ""
    private var webLink = ""
    private var recipeId = -1
    private var selectedImageUri: Uri? = null

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

        if (recipeId != -1){

            launch {
                context?.let {
                    var recipes = RecipeDatabase.getDatabase(it).recipeDao().getSpecificRecipe(recipeId)
                    _binding!!.recipeName.setText(recipes.recipename)
                    _binding!!.etIngredients.setText(recipes.ingredients)
                    _binding!!.etRecipeSteps.setText(recipes.recipesteps)
                    if (recipes.imgpath != ""){
                        selectedImagePath = recipes.imgpath!!
//                        _binding!!.imgNote.setImageBitmap(BitmapFactory.decodeFile(notes.imgpath))
                        selectedImageUri = recipes.imguri!!
                        GlideApp.with(requireContext()).load(recipes.imgpath).into(_binding!!.imgRecipe)
                       _binding!!.imgRecipe.visibility = View.VISIBLE
                        _binding!!.layoutImage.visibility = View.VISIBLE
                        _binding!!.imgDelete.visibility = View.VISIBLE
                    } else {
//                        _binding!!.imgRecipe.visibility = View.GONE
                        _binding!!.layoutImage.visibility = View.GONE
                        _binding!!.imgDelete.visibility = View.GONE
                    }

                    if (recipes.weblink != ""){
                        webLink = recipes.weblink!!
                        _binding!!.tvWebLink.text = recipes.weblink
                        _binding!!.layoutWebUrl.visibility = View.VISIBLE
                        _binding!!.etWebLink.setText(recipes.weblink)
                        _binding!!.imgUrlDelete.visibility = View.VISIBLE
                    } else {
                        _binding!!.layoutWebUrl.visibility = View.GONE
                        _binding!!.imgUrlDelete.visibility = View.GONE
                    }
                }
            }
        }
        // register broadcastmanager
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            BroadcastReceiver, IntentFilter("bottom_sheet_action")
        )
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        currentDate = sdf.format(Date())
        _binding!!.colorView.setBackgroundColor(Color.parseColor(selectedColor))

        _binding!!.tvDateTime.text = currentDate

        _binding!!.imgDone.setOnClickListener{
            // save note
            if (recipeId != -1){
                updateNote()

            } else {
                saveNote()
            }

        }

        _binding!!.imgBack.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        _binding!!.imgMore.setOnClickListener{
            var noteBottomSheetFragment = RecipeBottomSheetFragment.newInstance(recipeId)
            noteBottomSheetFragment.show(requireActivity().supportFragmentManager, "Note Bottom Sheet Fragment")
        }

        _binding!!.btnOk.setOnClickListener{
            if (_binding!!.etWebLink.text.toString().trim().isNotEmpty()) {
                checkWebUrl()
            } else {
                Toast.makeText(requireContext(), "Url is Required", Toast.LENGTH_SHORT).show()
            }
        }

        _binding!!.btnCancel.setOnClickListener{
            if (recipeId != -1){
                _binding!!.tvWebLink.visibility = View.VISIBLE
                _binding!!.layoutWebUrl.visibility = View.GONE
            } else {

                _binding!!.layoutWebUrl.visibility = View.GONE
            }
        }

        _binding!!.tvWebLink.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(_binding!!.etWebLink.text.toString()))
            startActivity(intent)
        }

        _binding!!.imgDelete.setOnClickListener{
            selectedImagePath = ""
            selectedImageUri = null
            _binding!!.layoutImage.visibility = View.GONE
        }

        _binding!!.imgUrlDelete.setOnClickListener{
            webLink = ""
            _binding!!.tvWebLink.visibility = View.GONE
            _binding!!.imgUrlDelete.visibility = View.GONE
            _binding!!.layoutWebUrl.visibility = View.GONE
        }
    }

    private fun updateNote() {

        launch {
            context?.let {
                var notes = RecipeDatabase.getDatabase(it).recipeDao().getSpecificRecipe(recipeId)
                notes.recipename = _binding!!.recipeName.text.toString()
                notes.ingredients = _binding!!.etIngredients.text.toString()
                notes.recipesteps = _binding!!.etRecipeSteps.text.toString()
                notes.datetime = currentDate
                notes.imgpath = selectedImagePath
                notes.weblink = webLink
                notes.imguri = selectedImageUri

                RecipeDatabase.getDatabase(it).recipeDao().updateRecipe(notes)
                _binding!!.recipeName.setText("")
                _binding!!.etIngredients.setText("")
                _binding!!.etRecipeSteps.setText("")
                _binding!!.imgRecipe.visibility = View.GONE
                _binding!!.layoutImage.visibility = View.GONE
                _binding!!.tvWebLink.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun saveNote() {
        if (_binding!!.recipeName.text.isNullOrEmpty()){
            Toast.makeText(context, "Note Title is Required", Toast.LENGTH_SHORT).show()
        }
        else if (_binding!!.etIngredients.text.isNullOrEmpty()){
            Toast.makeText(context, "Note Sub-Title is Required", Toast.LENGTH_SHORT).show()
        }
        else if (_binding!!.etRecipeSteps.text.isNullOrEmpty()){
            Toast.makeText(context, "Note Description is Required", Toast.LENGTH_SHORT).show()
        } else {

            launch {
                var notes = Recipes()
                notes.recipename = _binding!!.recipeName.text.toString()
                notes.ingredients = _binding!!.etIngredients.text.toString()
                notes.recipesteps = _binding!!.etRecipeSteps.text.toString()
                notes.datetime = currentDate
                notes.imgpath = selectedImagePath
                notes.weblink = webLink
                notes.imguri = selectedImageUri
                context?.let {
                    RecipeDatabase.getDatabase(it).recipeDao().insertRecipes(notes)
                    _binding!!.recipeName.setText("")
                    _binding!!.etIngredients.setText("")
                    _binding!!.etRecipeSteps.setText("")
                    _binding!!.imgRecipe.visibility = View.GONE
                    _binding!!.layoutImage.visibility = View.GONE
                    _binding!!.tvWebLink.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun deleteNote() {

        launch {
            context?.let {
                RecipeDatabase.getDatabase(it).recipeDao().deleteSpecificRecipe(recipeId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun checkWebUrl() {
        if (Patterns.WEB_URL.matcher(_binding!!.etWebLink.text.toString()).matches()){
            _binding!!.layoutWebUrl.visibility = View.GONE
            _binding!!.etWebLink.isEnabled = false
            webLink = _binding!!.etWebLink.text.toString()
            _binding!!.tvWebLink.visibility = View.VISIBLE
            _binding!!.tvWebLink.text = _binding!!.etWebLink.text.toString()
        } else {
            Toast.makeText(requireContext(), "Url is not valid", Toast.LENGTH_SHORT).show()
        }
    }

    // to receive actions
    private val BroadcastReceiver : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var actionColor = intent!!.getStringExtra("action")

            when (actionColor!!) {

                "Image" -> {
                    readStorageTask()
                    _binding!!.layoutWebUrl.visibility = View.GONE
                }

                "WebUrl" -> {
                    _binding!!.layoutWebUrl.visibility = View.VISIBLE
                }

                "DeleteNote" -> {
                    deleteNote()
                }

                else -> {
                    _binding!!.layoutWebUrl.visibility = View.GONE
                    _binding!!.layoutImage.visibility = View.GONE
                    _binding!!.imgRecipe.visibility = View.GONE
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    _binding!!.colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
            }
        }

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(BroadcastReceiver)
        super.onDestroy()
    }

    private fun hasReadStoragePerm(): Boolean{
        return EasyPermissions.hasPermissions(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun readStorageTask() {
        if (hasReadStoragePerm()){
            pickImageFromGallery()

        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                getString(R.string.storage_permission_text),
                READ_STORAGE_PERM,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun pickImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null){
//             startActivityForResult(intent, REQUEST_CODE_IMAGE)
            resultLauncher.launch(intent)
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath:String? = null
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null){
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null) {
                    try {
//                         var inputStream =
//                             requireActivity().contentResolver.openInputStream(selectedImageUrl)
//                         var bitmap = BitmapFactory.decodeStream(inputStream)
//                         _binding!!.imgNote.setImageBitmap(bitmap)
                        GlideApp.with(requireContext()).load(selectedImageUrl).into(_binding!!.imgRecipe)
                        _binding!!.imgRecipe.visibility = View.VISIBLE
                        _binding!!.layoutImage.visibility = View.VISIBLE

                        selectedImagePath = getPathFromUri(selectedImageUrl)!!
                        selectedImageUri = selectedImageUrl!!


                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

//     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//         super.onActivityResult(requestCode, resultCode, data)
//         if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK){
//             if (data != null){
//                 var selectedImageUrl = data.data
//                 if (selectedImageUrl != null){
//                     try {
//                         var inputStream = requireActivity().contentResolver.openInputStream(selectedImageUrl)
//                         var bitmap = BitmapFactory.decodeStream(inputStream)
//                         _binding!!.imgNote.setImageBitmap(bitmap)
//                         _binding!!.imgNote.visibility = View.VISIBLE
//                         _binding!!.layoutImage.visibility = View.VISIBLE
//
//                         selectedImagePath = getPathFromUri(selectedImageUrl)!!
//                     } catch (e:Exception) {
//                         Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//                     }
//                 }
//             }
//         }
//     }

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
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)){
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }
}