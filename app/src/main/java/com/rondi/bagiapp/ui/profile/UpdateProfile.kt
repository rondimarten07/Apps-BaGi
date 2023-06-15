package com.rondi.bagiapp.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rondi.bagiapp.R.string
import com.bumptech.glide.Glide
import com.rondi.bagiapp.R.drawable
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.data.remote.response.Profile
import com.rondi.bagiapp.databinding.ActivityUpdateProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import com.rondi.bagiapp.utils.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
@Suppress("DEPRECATION")
class UpdateProfile : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var token: String = ""
    private var getFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val profileResponse = intent.getParcelableExtra<Profile>("profileResponse")


        val checkPhoto = profileResponse?.avatar
        if (checkPhoto != null){
            Glide.with(this).load(profileResponse.avatar).into(binding.editPhotoProfile)
        }else{
            binding.editPhotoProfile.setImageResource(drawable.user)
        }

        binding.editNama.setText(profileResponse?.nama)
        binding.editUsername.setText(profileResponse?.username)
        binding.editPhone.setText(profileResponse?.phone)
        binding.editAlamat.setText(profileResponse?.loc)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        lifecycleScope.launchWhenCreated{
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                    binding.btnUpdate.setOnClickListener { uploadImage("Bearer $token") }
                }
            }
        }

        binding.btnOpenGallery.setOnClickListener { startGallery() }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
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
                "avatar",
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



}
