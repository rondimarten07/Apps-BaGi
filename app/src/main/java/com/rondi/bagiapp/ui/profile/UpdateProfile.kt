package com.rondi.bagiapp.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.rondi.bagiapp.R.string
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.Glide
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.Profile
import com.rondi.bagiapp.databinding.ActivityUpdateProfileBinding
import com.rondi.bagiapp.ui.camera.CameraActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import com.rondi.bagiapp.utils.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
@ExperimentalPagingApi
@Suppress("DEPRECATION")
class UpdateProfile : AppCompatActivity() {
    private var _binding: ActivityUpdateProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var token: String = ""
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(string.message_not_permitted),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileResponse = intent.getParcelableExtra<Profile>("profileResponse")


        Glide.with(this).load(profileResponse?.avatar).into(binding.editPhotoProfile)
        binding.editNama.setText(profileResponse?.nama)
        binding.editUsername.setText(profileResponse?.username)
        binding.editPhone.setText(profileResponse?.phone)
        binding.editAlamat.setText(profileResponse?.loc)
//        binding.editEmail.setText(profileResponse?.email)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                    binding.btnUpdate.setOnClickListener { uploadImage("Bearer $token") }
                }
            }
        }

        binding.btnOpenCamera.setOnClickListener { startCameraX() }
        binding.btnOpenGallery.setOnClickListener { startGallery() }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }


    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.editPhotoProfile.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UpdateProfile)
                getFile = myFile
                binding.editPhotoProfile.setImageURI(uri)
            }
        }
    }


    private fun uploadImage(token: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val name = binding.editNama.text
            val username = binding.editUsername.text
            val phone = binding.editPhone.text
            val loc = binding.editAlamat.text

            val nameMediaTyped =
                name.toString().toRequestBody("text/plain".toMediaType())
            val usernameMediaTyped =
                username.toString().toRequestBody("text/plain".toMediaType())
            val phoneMediaTyped =
                phone.toString().toRequestBody("text/plain".toMediaType())
            val locMediaTyped =
                loc.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )


            viewModel.updateProfile(
                token,
                imageMultipart,
                nameMediaTyped,
                usernameMediaTyped,
                phoneMediaTyped,
                locMediaTyped
            ).observe(this) { response ->
                    when (response) {
                        is ApiResponse.Loading -> {
                            showLoading(true)
                        }
                        is ApiResponse.Success -> {
                            showLoading(false)
                            showToast(getString(string.message_succes_updates))
                            finish()
                        }
                        is ApiResponse.Error -> {
                            showLoading(false)
                            showOKDialog(getString(string.message_failed_updates), response.error)
                        }
                        else -> {
                            showLoading(false)
                            showToast(getString(string.message_unknown_state))
                        }
                    }

                }
        } else {
            showOKDialog(getString(string.title_message), getString(string.message_pick_image))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}