package com.example.attendanceapp.ui.selfie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.Button
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
    private var userId: String? = null
    private var alertDialog: AlertDialog? = null

    private var errorMessageShown = false

    private val viewModel by viewModels<SelfieViewModel> {
        ViewModelFactory(requireContext())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
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

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {

            userId = user.userId
            var username = user.username

            }
        }

        binding.btnTakeSelfie.setOnClickListener{
            takePhoto()
        }

        binding.progressBar.visibility = View.GONE
        errorMessageShown = false

        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    if (alertDialog?.isShowing == true) {
                        alertDialog?.dismiss()
                        alertDialog = null
                    }
                    errorMessageShown = false
                    alertDialog = showProcessDialog()
                    binding.viewFinder.visibility = View.GONE
                    binding.imageViewSelfie.visibility = View.VISIBLE
//                        binding.imageViewSelfie.setImageBitmap(bitmap)
                    binding.blackOverlay.visibility = View.VISIBLE

                    Log.d("Selfie Fragment", "Loading upload image...")
//                                binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
//                                binding.progressBar.visibility = View.GONE
                    alertDialog?.dismiss()
                    alertDialog = null
                    val resultUploadImage = result.data
                    showToast(resultUploadImage.status)
                    if (isAdded && findNavController().currentDestination?.id == R.id.selfieFragment) {
                        findNavController().navigate(R.id.action_selfieFragment_to_successFragment)
                    }
                }

                is Result.Error -> {
                    binding.imageViewSelfie.visibility = View.GONE
                    binding.blackOverlay.visibility = View.GONE
                    binding.viewFinder.visibility = View.VISIBLE
                    alertDialog?.dismiss()
                    alertDialog = null
                    binding.progressBar.visibility = View.GONE
                    if (!errorMessageShown) {
                        errorMessageShown = true
                        Log.e("Selfie Fragment", "Error: ${result.error}")
                        showToast(result.error)

                    }
                }
            }
        }

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

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1080, 2192))
                .build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        errorMessageShown = false
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFile(requireContext())

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {

//                    showToast("Success Taking Selfie")

                    val uri = output.savedUri ?: return

                    // Convert URI to Bitmap
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

                    // Crop image based on OverlayView
                    val croppedBitmap = cropBitmapToOverlay(bitmap)

                    // Save the cropped image
                    val croppedFile = saveBitmapToFile(requireContext(), croppedBitmap)

                    val uriCroppedfile = Uri.fromFile(croppedFile)

                    viewModel.uploadSelfie(
                        requireContext(),
                        uriCroppedfile!!,
                        userId!!
                        )
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
        // Resize bitmap ke ukuran ViewFinder
        val viewWidth = binding.viewFinder.width
        val viewHeight = binding.viewFinder.height
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, viewWidth, viewHeight, true)

        // Ukuran cropping dari OverlayView
        val rectWidth = (viewWidth * 0.85f).toInt()    // Sesuai dengan OverlayView
        val rectHeight = (viewHeight * 0.55f).toInt()

        val left = ((viewWidth - rectWidth) / 2).toInt()
        val top = ((viewHeight - rectHeight) / 2).toInt()

        Log.d("DEBUG_SIZE", "Original Bitmap size: ${bitmap.width}x${bitmap.height}")
        Log.d("DEBUG_SIZE", "Resized Bitmap size: ${resizedBitmap.width}x${resizedBitmap.height}")
        Log.d("DEBUG_SIZE", "ViewFinder size: ${viewWidth}x${viewHeight}")
        Log.d("DEBUG_SIZE", "OverlayView size: ${rectWidth}x${rectHeight}")
        Log.d("DEBUG_SIZE", "Crop area: left=$left, top=$top, width=$rectWidth, height=$rectHeight")

        return Bitmap.createBitmap(resizedBitmap, left, top, rectWidth, rectHeight)
    }


    private fun showProcessDialog():AlertDialog{
        val dialogView = layoutInflater.inflate(R.layout.processing_selfie_dialog, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val alertDialog = builder.create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        alertDialog.show()

        alertDialog.window?.setLayout(
            (requireContext().resources.displayMetrics.widthPixels * 0.85).toInt(), // 85% dari layar
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return alertDialog
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        val EXTRA_URI = "URI"
    }

}