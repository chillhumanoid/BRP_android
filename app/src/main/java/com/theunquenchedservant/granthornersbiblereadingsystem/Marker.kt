package com.theunquenchedservant.granthornersbiblereadingsystem

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.boolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.increaseIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.checkDate
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.dates.getDate


object Marker {
    private val isLogged = FirebaseAuth.getInstance().currentUser
    fun markAll() {
        for (i in 1..10) {
            setIntPref("list${i}Done", 1)
        }
        setIntPref("listsDone", 10)
        if (getIntPref("dailyStreak") == 0) {
            if(!checkDate("current", false)){
                val currentStreak = increaseIntPref("currentStreak", 1)
                setStringPref("dateChecked", getDate(0,false))
                if(currentStreak > getIntPref("maxStreak"))
                    setIntPref("maxStreak", currentStreak)
            }
            setIntPref("dailyStreak", 1)
        }
        if (isLogged != null) {
            val db = FirebaseFirestore.getInstance()
            val updateValues = mutableMapOf<String, Any>()
            for (i in 1..10) {
                updateValues["list${i}Done"] = 1
            }
            updateValues["listsDone"] = 10
            updateValues["dateChecked"] = getDate(0,false)
            updateValues["dailyStreak"] = getIntPref("dailyStreak")
            updateValues["currentStreak"] = getIntPref("currentStreak")
            updateValues["maxStreak"] = getIntPref("maxStreak")
            db.collection("main").document(isLogged.uid).update(updateValues)
                    .addOnSuccessListener {
                        log("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
    }
    fun markSingle(cardDone: String) {
        val db = FirebaseFirestore.getInstance()
        val allowPartial = boolPref("allow_partial_switch", null)
        val listsDone = increaseIntPref("listsDone", 1)
        if (getIntPref(cardDone) != 1) {
            setIntPref(cardDone, 1)
            setStringPref("dateChecked", getDate(0, false))
            if (allowPartial || listsDone == 10) {
                if (getIntPref("dailyStreak") == 0) {
                    val currentStreak = increaseIntPref("currentStreak", 1)
                    if (currentStreak > getIntPref("maxStreak")) {
                        setIntPref("maxStreak", currentStreak)
                    }
                    setIntPref("dailyStreak", 1)
                }
            }
            if (isLogged != null) {
                val data = mutableMapOf<String, Any>()
                data["maxStreak"] = getIntPref("maxStreak")
                data["currentStreak"] = getIntPref("currentStreak")
                data["dailyStreak"] = 1
                data["listsDone"] = listsDone
                data["dateChecked"] = getDate(0, false)
                data[cardDone] = 1
                db.collection("main").document(isLogged.uid).update(data)
            }
        }
    }
}