package com.example.attendanceapp.ui.attendance_history

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.example.attendanceapp.R
import com.example.attendanceapp.data.remote.response.AttendanceDataItem
import com.example.attendanceapp.databinding.FragmentAttendanceHistoryBinding
import com.example.attendanceapp.repository.Result
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AttendanceHistoryFragment : Fragment() {

    private var _binding: FragmentAttendanceHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var attendance_data: List<AttendanceDataItem>

    private val viewModel: AttendanceHistoryViewModel by viewModels() {
        ViewModelFactory(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentAttendanceHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendarView = binding.calendarView


//        val list = listOf(
//        CalendarDay(Calendar.getInstance()).apply {
//            selectedBackgroundResource = R.drawable.ic_circle_blue
//        })
//
//        calendarView.setCalendarDays(list)

        binding.calendarView.visibility = View.GONE
        binding.textViewDate.visibility = View.GONE
        binding.textViewDay.visibility = View.GONE
        binding.textViewMonth.visibility = View.GONE
        binding.textViewPresent.visibility = View.GONE
        binding.textViewTime.visibility = View.GONE

        // Atur tanggal yang bisa dipilih
//        val calendar = Calendar.getInstance()
//        calendarView.setDate(calendar)

        viewModel.getSession().observe(viewLifecycleOwner) { userModel ->

            val userId = userModel.userId

            viewModel.getAttendanceHistory(userId).observe(viewLifecycleOwner) {  result ->
                when (result) {
                    is Result.Loading -> {
                        Log.d("Selfie Fragment", "Loading upload image...")
                        binding.progressBarAttendanceHistory.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBarAttendanceHistory.visibility = View.GONE
                        val attendanceData = result.data
                        markPresentDays(calendarView, attendanceData)
                        attendance_data = attendanceData
                        binding.calendarView.visibility = View.VISIBLE
                        playCalendarOpacityAnimation()

                    }

                    is Result.Error -> {
                        binding.progressBarAttendanceHistory.visibility = View.GONE
                        Log.e("AddStoryActivity", "Error: ${result.error}")
                        showToast(result.error)
                    }
                }
            }
        }

//        binding.calendarView

        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                val clickedDayCalendar = calendarDay.calendar

                val selectedDay = CalendarDay(clickedDayCalendar).apply {
                    backgroundDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.selected_day_background)
                    labelColor = R.color.white
                }

                calendarView.setCalendarDays(listOf(selectedDay))


                // Format tanggal ke String agar lebih mudah dibaca
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dateFormatting = SimpleDateFormat("dd", Locale.getDefault())
                val monthFormatting = SimpleDateFormat("MMMM", Locale.getDefault())
                val dayFormatting = SimpleDateFormat("EEEE", Locale.getDefault())
                val clickedDate = dateFormat.format(clickedDayCalendar.time)

                // Tampilkan toast dengan tanggal yang diklik
//                Toast.makeText(requireContext(), "Tanggal yang diklik: $clickedDate", Toast.LENGTH_SHORT).show()

                val attendance_time = searchAttendance(clickedDate, attendance_data)

                if (attendance_time["Return_value"] != "Tidak ada data kehadiran") {

                    binding.textViewDate.visibility = View.VISIBLE
                    binding.textViewDay.visibility = View.VISIBLE
                    binding.textViewMonth.visibility = View.VISIBLE
                    binding.textViewPresent.visibility = View.VISIBLE
                    binding.textViewTime.visibility = View.VISIBLE

                    if(attendance_time["status"] == "absent"){
                        binding.textViewTime.text = "-"
                        binding.textViewDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    }
                    else{
                        binding.textViewTime.text = attendance_time["time"].toString()
                        binding.textViewDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.base_color_600))
                    }


                    binding.textViewDate.text = dateFormatting.format(clickedDayCalendar.time)
                    binding.textViewMonth.text = monthFormatting.format(clickedDayCalendar.time)
                    binding.textViewDay.text  = dayFormatting.format(clickedDayCalendar.time)

                    binding.textViewPresent.text = attendance_time["status"].toString().replaceFirstChar { it.uppercase() }

                    playOpacityAnimation()
                    playTranslateAninmation()
                }

                else{
                    binding.textViewDate.visibility = View.GONE
                    binding.textViewDay.visibility = View.GONE
                    binding.textViewMonth.visibility = View.GONE
                    binding.textViewPresent.visibility = View.GONE
                    binding.textViewTime.visibility = View.GONE
                }


            }
        })


        return root
    }


    private fun markPresentDays(calendarView: CalendarView, attendanceData: List<AttendanceDataItem>) {
        val presentDates = mutableListOf<Calendar>()
        val eventDays = mutableListOf<EventDay>()

        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

        for (item in attendanceData) {
            try {
                val date = dateFormat.parse(item.createdAt)
                if (date != null && item.status.equals("present", ignoreCase = true)) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    presentDates.add(calendar)

                    // Tambahkan ikon atau lingkaran pada tanggal "present"
                    eventDays.add(EventDay(calendar, R.drawable.ic_circle_blue))
                }
                else if(date != null && item.status.equals("absent", ignoreCase = true)){
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    presentDates.add(calendar)
                    eventDays.add(EventDay(calendar, R.drawable.ic_circle_red))
                }
            } catch (e: Exception) {
                Log.e("DateParsing", "Error parsing date: ${item.createdAt}")
            }
        }

        // Tandai tanggal dengan status "present"
//        calendarView.setHighlightedDays(presentDates)
        calendarView.setEvents(eventDays)
    }


    private fun searchAttendance(clickDate: String, attendanceData: List<AttendanceDataItem>): Map<String, Any> {
        val inputDateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH) // Format asli dari API
        val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Format yang diklik user
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // Format waktu

        for (item in attendanceData) {
            try {
                val date = inputDateFormat.parse(item.createdAt) // Parsing dari format API
                if (date != null && clickDate == outputDateFormat.format(date)) {
//                    return timeFormat.format(date)

                    return mutableMapOf(
                        "time" to timeFormat.format(date),
                        "status" to item.status)

                }
            } catch (e: Exception) {
                Log.e("DateParsing", "Error parsing date: ${item.createdAt}")
            }
        }
        return mutableMapOf(
            "Return_value" to "Tidak ada data kehadiran")
    }

    private fun playOpacityAnimation() {

        binding.textViewDate.alpha = 0f
        binding.textViewDay.alpha = 0f
        binding.textViewMonth.alpha = 0f
        binding.textViewPresent.alpha = 0f
        binding.textViewTime.alpha = 0f




        val tvDate =
            ObjectAnimator.ofFloat(binding.textViewDate, View.ALPHA, 1f).setDuration(1500)

        val tvDay=
            ObjectAnimator.ofFloat(binding.textViewDay, View.ALPHA, 1f).setDuration(1500)

        val tvMonth=
            ObjectAnimator.ofFloat(binding.textViewMonth, View.ALPHA, 1f).setDuration(1500)

        val tvPresent=
            ObjectAnimator.ofFloat(binding.textViewPresent, View.ALPHA, 1f).setDuration(1500)

        val tvTime=
            ObjectAnimator.ofFloat(binding.textViewTime, View.ALPHA, 1f).setDuration(1500)


        AnimatorSet().apply {
            playTogether(
                tvDate, tvDay,tvMonth, tvPresent, tvTime

            )
            start()

        }
    }

    private fun playTranslateAninmation(){
        ObjectAnimator.ofFloat(binding.textViewDate, View.TRANSLATION_Y, 50f, -50f).apply {
            duration = 1000
//            repeatCount = ObjectAnimator
//            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.textViewDay, View.TRANSLATION_Y, 50f, -50f).apply {
            duration = 1000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.textViewMonth, View.TRANSLATION_Y, 50f, -50f).apply {
            duration = 1000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.textViewPresent, View.TRANSLATION_Y, 50f, -50f).apply {
            duration = 1000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.textViewTime, View.TRANSLATION_Y, 50f, -50f).apply {
            duration = 1000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }


    private fun playCalendarOpacityAnimation() {
        binding.calendarView.alpha = 0f

        ObjectAnimator.ofFloat(binding.calendarView, View.ALPHA, 1f).setDuration(800).start()


    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}