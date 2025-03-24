package com.example.attendanceapp.ui.validation

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.attendanceapp.databinding.FragmentValidationBinding
import com.example.attendanceapp.ui.selfie.SelfieFragment


class ValidationFragment : Fragment() {

    private var _binding: FragmentValidationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentValidationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val stringUri = arguments?.getString(SelfieFragment.EXTRA_URI)

        if (!stringUri.isNullOrEmpty()) {
            // Mengubah string URI menjadi objek Uri
            val uri = Uri.parse(stringUri)

            // Menampilkan gambar di ImageView menggunakan setImageURI()
            binding.imageView4.setImageURI(uri)
        }

        return root
    }


}