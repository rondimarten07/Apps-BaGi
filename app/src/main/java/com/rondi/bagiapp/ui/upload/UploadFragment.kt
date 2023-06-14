package com.rondi.bagiapp.ui.upload

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.ExperimentalPagingApi
import com.rondi.bagiapp.R
import com.rondi.bagiapp.data.remote.ApiResponse
import com.rondi.bagiapp.databinding.FragmentUploadBinding
import com.rondi.bagiapp.ml.Model
import com.rondi.bagiapp.ui.camera.CameraActivity
import com.rondi.bagiapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.math.min

@Suppress("DEPRECATION")
@AndroidEntryPoint
@ExperimentalPagingApi
class UploadFragment : Fragment() {
    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UploadViewModel by viewModels()
    private var token: String = ""
    private var getFile: File? = null
    private val imageSize = 416
    private lateinit var dialog: Dialog
    private lateinit var dialogError: Dialog

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.message_not_permitted),
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnSelectImage.setOnClickListener {
            startCameraX()
        }

        dialogSuccess()

        dialogError()


        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                    binding.btnUpload.setOnClickListener { uploadImage("Bearer $token") }
                }
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
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

                val imageBitmap = BitmapFactory.decodeFile(file.path)
                val dimension = min(imageBitmap.width, imageBitmap.height)
                val thumbnail = ThumbnailUtils.extractThumbnail(
                    imageBitmap,
                    dimension,
                    dimension
                )

                binding.imgUpload.setImageBitmap(thumbnail)

                val scaledImage = Bitmap.createScaledBitmap(thumbnail, imageSize, imageSize, false)
                classifyImage(scaledImage)
            }
        }
    }

    private fun classifyImage(image: Bitmap) {
        try {
            val model = Model.newInstance(requireContext())

            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)

            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val value = intValues[pixel++]
                    byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val confidences = outputFeature0.floatArray

            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                Log.d("Confidences", "Confidence[$i]: ${confidences[i]}")
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                    Log.d("max pos", "max pos[$i]")
                }
            }
            val classes =
                arrayOf("defected", "hat", "jacket", "shirt", "pants", "shorts", "skirt", "dress", "shoe", "sepatu")
            binding.tvResult.text = classes[maxPos]

            binding.edKategori.setText(classes[maxPos])

            val resultMax = maxConfidence * 100
            val resultMaxInt = resultMax.toInt()
            binding.resultBar.progress = resultMaxInt
            binding.tvResultBar.text = "$resultMaxInt%"

            var s = ""
            for (i in classes.indices) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100)
            }

            binding.tvConfidence.text = s

            model.close()
        } catch (e: IOException) {
            Log.e("error", e.toString())
        }
    }

    private fun dialogSuccess() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dialog_success)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_edit_text)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.window?.attributes = layoutParams

        val btnOk = dialog.findViewById<Button>(R.id.btn_ok)

        binding.apply {
            btnOk.setOnClickListener {
                dialog.dismiss()
                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                navController.navigate(R.id.action_navigation_upload_to_navigation_home)
            }
        }
    }

    private fun dialogError() {
        dialogError = Dialog(requireContext())
        dialogError.setContentView(R.layout.custom_dialog_error)
        dialogError.window?.setBackgroundDrawableResource(R.drawable.bg_edit_text)
        dialogError.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogError.setCancelable(false)
        dialogError.window?.attributes?.windowAnimations = R.style.DialogAnimation
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialogError.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogError.window?.attributes = layoutParams

        val btnOk = dialogError.findViewById<Button>(R.id.btn_ok)

        binding.apply {
            btnOk.setOnClickListener {
                dialogError.dismiss()
            }
        }
    }

    private fun uploadImage(token: String) {
        val kategori = binding.edKategori.text.toString()
        val title = binding.edTitle.text
        val description = binding.edDescription.text
        val uniqueFileName = UUID.randomUUID().toString()


        if (getFile == null) {
            showOKDialog(getString(R.string.title_message), getString(R.string.message_pick_image))
        } else if (kategori == "defected") {
            dialogError.show()
        } else {
            val file = reduceFileImage(getFile as File)

            val kategoriMediaTyped =
                kategori.toRequestBody("text/plain".toMediaType())

            val titleMediaTyped =
                title.toString().toRequestBody("text/plain".toMediaType())
            val descriptionMediaTyped =
                description.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart = MultipartBody.Part.createFormData(
                "image",
                uniqueFileName + file.name,
                requestImageFile
            )


            viewModel.uploadItems(
                token,
                imageMultipart,
                titleMediaTyped,
                descriptionMediaTyped,
                kategoriMediaTyped

            ).observe(requireActivity()) { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        showLoading(true)
                    }
                    is ApiResponse.Success -> {
                        showLoading(false)
                        dialog.show()
                        clearText()

                    }
                    is ApiResponse.Error -> {
                        showLoading(false)
                        showOKDialog(getString(R.string.message_failed_upload), response.error)
                    }
                    else -> {
                        showLoading(false)
                        showToast(getString(R.string.message_unknown_state))
                    }
                }

            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            edTitle.isEnabled = !isLoading
            edDescription.isEnabled = !isLoading
            btnUpload.isEnabled = !isLoading
            btnSelectImage.isEnabled = !isLoading

            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun clearText(){
        binding.apply {
            edTitle.setText("")
            edDescription.setText("")
            edKategori.setText("")
        }
    }

        companion object {
            const val CAMERA_X_RESULT = 200

            private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
            private const val REQUEST_CODE_PERMISSIONS = 10
        }
    }

