package com.example.attendanceapp.ui.home

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import coil.decode.GifDecoder
import coil.load
import coil.request.CachePolicy
import com.bumptech.glide.Glide
import com.example.attendanceapp.R
import com.example.attendanceapp.databinding.FragmentHomeBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if(checkTimeRange() && isTodayWeekday()){
            binding.gifImageView.visibility = View.GONE
            binding.lottieAnimationView.visibility = View.VISIBLE
        }
        else{
            binding.gifImageView.visibility = View.VISIBLE
            binding.lottieAnimationView.visibility = View.GONE
        }

        binding.tvDay.text = getCurrentDate()

        binding.tvTime.text = getCurrentTime()

        binding.btnCheckIn.setOnClickListener { view ->
            if(checkTimeRange() && isTodayWeekday()){
                view.findNavController().navigate(R.id.action_navigation_home_to_mapsFragment)
            }
            else{
//                showToast("Anda tidak bisa melakukan absensi saat ini")
                showInfoDialog()
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun checkTimeRange(): Boolean {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Waktu saat ini
        val currentTime = sdf.parse(sdf.format(Date())) ?: return false

        // Rentang waktu yang diperbolehkan
        val startTime = sdf.parse("07:00") ?: return false
        val endTime = sdf.parse("24:00") ?: return false

        // Mengecek apakah waktu saat ini dalam rentang
        return currentTime in startTime..endTime
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }



    fun isTodayWeekday(): Boolean {
        val calendar = Calendar.getInstance() // Mendapatkan waktu saat ini
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY
    }

    private fun showInfoDialog(){
        val dialogView = layoutInflater.inflate(R.layout.map_alert_dialog_box_home, null)

        val dismissButton = dialogView.findViewById<Button>(R.id.button_alert_dialog_box_map_home)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val alertDialog = builder.create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dismissButton.setOnClickListener{
            alertDialog.dismiss()
        }

        alertDialog.show()

        alertDialog.window?.setLayout(
            (requireContext().resources.displayMetrics.widthPixels * 0.85).toInt(), // 85% dari layar
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}