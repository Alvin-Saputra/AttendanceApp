package com.example.attendanceapp.ui.map

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "GeofenceBroadcast"
        const val ACTION_GEOFENCE_EVENT = "GeofenceEvent"
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }

            val geofenceTransition = geofencingEvent.geofenceTransition

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition ==
                Geofence.GEOFENCE_TRANSITION_DWELL || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                val geofenceTransitionString =
                    when (geofenceTransition) {
                        Geofence.GEOFENCE_TRANSITION_ENTER -> "Anda telah memasuki area"
                        Geofence.GEOFENCE_TRANSITION_DWELL -> "Anda telah di dalam area"
                        Geofence.GEOFENCE_TRANSITION_EXIT -> "Anda telah dari keluar area"
                        else -> "Invalid transition type"
                    }

                Log.i(TAG, geofenceTransitionString)
                val broadcastIntent = Intent("GeofenceStatusUpdate")
                broadcastIntent.putExtra("status", geofenceTransitionString)
                context.sendBroadcast(broadcastIntent) // Mengirimkan broadcast

            }


        }
    }
}