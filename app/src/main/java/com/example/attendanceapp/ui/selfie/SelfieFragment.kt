package com.example.attendanceapp.ui.selfie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.attendanceapp.R
import com.example.attendanceapp.databinding.FragmentSelfieBinding
import com.example.attendanceapp.utility.createCustomTempFile
import com.example.attendanceapp.utility.saveBitmapToFile
import com.example.attendanceapp.repository.Result

class SelfieFragment : Fragment() {

    private var _binding: FragmentSelfieBinding? = null
    private val binding get() = _binding!!
    private var imageCapture: ImageCapture? = null

    private val viewModel by viewModels<SelfieViewModel> {
        ViewModelFactory(requireContext())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG).show()
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSelfieBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        else{
            startCamera()
        }

        binding.btnTakeSelfie.setOnClickListener{
            takePhoto()
        }

        binding.progressBar.visibility = View.GONE

        return root
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            imageCapture = ImageCapture.Builder().build()

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createCustomTempFile(requireContext())
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    showToast("Success Taking Selfie")

                    val uri = output.savedUri ?: return

//                    // Convert URI to Bitmap
//                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
//
//                    // Crop image based on OverlayView
//                    val croppedBitmap = cropBitmapToOverlay(bitmap)
//
//                    // Save the cropped image
//                    val croppedFile = saveBitmapToFile(requireContext(), croppedBitmap)
//
//                    val stringUriCroppedfile = Uri.fromFile(croppedFile)

//                    val mBundle = Bundle()
//                    mBundle.putString(EXTRA_URI, stringUriCroppedfile)
//                    findNavController().navigate(R.id.action_selfieFragment_to_validationFragment, mBundle)

//                    val intent = Intent()
//                    intent.putExtra(EXTRA_CAMERAX_IMAGE, output.savedUri.toString())
//                    setResult(CAMERAX_RESULT, intent)
//                    finish()

                    viewModel.uploadSelfie(
                        requireContext(),
                        uri!!
                        ).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                Log.d("Selfie Fragment", "Loading upload image...")
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val resultUploadImage = result.data
                                showToast(resultUploadImage.status)
                                findNavController().navigate(R.id.action_selfieFragment_to_successFragment)
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Log.e("AddStoryActivity", "Error: ${result.error}")
                                showToast(result.error)
                            }
                        }
                    }
                }
                override fun onError(exc: ImageCaptureException) {
//                    Toast.makeText(
//                        this@CameraActivity,
//                        "Gagal mengambil gambar.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }


    private fun cropBitmapToOverlay(bitmap: Bitmap): Bitmap {
        // Ukuran view kamera
        val viewWidth = binding.viewFinder.width
        val viewHeight = binding.viewFinder.height

        // Ukuran cropping dari OverlayView
        val rectWidth = viewWidth * 0.8f    // Sesuai dengan OverlayView
        val rectHeight = viewHeight * 0.4f


        val left = ((viewWidth - rectWidth) / 2).toInt()
        val top = ((viewHeight - rectHeight) / 2).toInt()

        // Skala bitmap agar sesuai dengan ukuran preview
        val scaleX = bitmap.width.toFloat() / viewWidth
        val scaleY = bitmap.height.toFloat() / viewHeight

        val cropLeft = (left * scaleX).toInt()
        val cropTop = (top * scaleY).toInt()
        val cropWidth = (rectWidth * scaleX).toInt()
        val cropHeight = (rectHeight * scaleY).toInt()

        return Bitmap.createBitmap(bitmap, cropLeft, cropTop, cropWidth, cropHeight)
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        val EXTRA_URI = "URI"
    }

}