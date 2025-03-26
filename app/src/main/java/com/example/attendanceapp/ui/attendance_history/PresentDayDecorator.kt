//package com.example.attendanceapp.ui.attendance_history
//
//import android.graphics.Color
//import android.text.style.ForegroundColorSpan
//import com.prolificinteractive.materialcalendarview.CalendarDay
//import com.prolificinteractive.materialcalendarview.DayViewDecorator
//import com.prolificinteractive.materialcalendarview.DayViewFacade
//
//class PresentDayDecorator(private val presentDates: List<CalendarDay>) : DayViewDecorator {
//    override fun shouldDecorate(day: CalendarDay?): Boolean {
//        return presentDates.contains(day)
//    }
//
//    override fun decorate(view: DayViewFacade?) {
//        view?.addSpan(ForegroundColorSpan(Color.BLUE))  // Ubah warna teks ke biru
//    }
//}
