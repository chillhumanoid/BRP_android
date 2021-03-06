package com.theunquenchedservant.granthornersbiblereadingsystem.utilities

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_CHAPTERS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.getBooks
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref

object BibleStatsReset {
    private val isLogged = Firebase.auth.currentUser

    fun resetBook(bookName: String, testament: String, hardReset: Boolean = false, internal: Boolean = false, updateValues: MutableMap<String, Any> = mutableMapOf()):MutableMap<String, Any>{
        val chapters = BOOK_CHAPTERS[bookName] ?: error("")
        for(chapter in 1..chapters){
            updateValues["${bookName}${chapter}Read"] = false
            if(hardReset) {
                updateValues["${bookName}${chapter}AmountRead"] = 0

            }
        }
        val testamentChaptersDone = getIntPref(name="${testament}ChaptersRead")
        val wholeChaptersDone = getIntPref(name="totalChaptersRead")
        val chaptersDone = getIntPref(name="${bookName}ChaptersRead")
        if(!getBoolPref(name="${bookName}DoneTestament")){
            updateValues["${testament}ChaptersRead"] = testamentChaptersDone - chaptersDone
        }else{
            updateValues["${testament}ChaptersRead"] = testamentChaptersDone - chapters
        }
        if(!getBoolPref(name="${bookName}DoneWhole")){
            updateValues["totalChaptersRead"] = wholeChaptersDone - chaptersDone
        }else{
            updateValues["totalChaptersRead"] = wholeChaptersDone-chapters
        }
        updateValues["${bookName}ChaptersRead"] = 0
        if (hardReset){
            updateValues["${bookName}AmountRead"] = 0
        }
        if (isLogged != null && !internal) {
            val db = Firebase.firestore
            db.collection("main").document(isLogged.uid).update(updateValues)
                    .addOnSuccessListener {
                        debugLog("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
        return updateValues
    }

    fun resetTestament(testament: String, hardReset: Boolean=false, internal: Boolean=false, updateValues: MutableMap<String, Any> = mutableMapOf()) :MutableMap<String, Any>{
        val books = getBooks(testament)!!
        var updateValueUpdated = updateValues
        for(book in books){
            updateValueUpdated = resetBook(book, testament, hardReset, internal=true, updateValueUpdated)
        }

        updateValueUpdated["${testament}ChaptersRead"] = 0
        if(hardReset){

            updateValueUpdated["${testament}AmountRead"] = 0
        }
        if (!internal) {
            val db = Firebase.firestore
            db.collection("main").document(isLogged!!.uid).update(updateValueUpdated)
                    .addOnSuccessListener {
                        debugLog("Successful update")
                    }
                    .addOnFailureListener {
                        val error = it
                        Log.w("PROFGRANT", "Failure writing to firestore", error)
                    }
        }
        return updateValueUpdated
    }

    fun resetBible(hardReset: Boolean=false) {
        var updateValues = mutableMapOf<String, Any>()
        updateValues = resetTestament(testament = "new", hardReset, internal = true, updateValues)
        updateValues = resetTestament(testament = "old", hardReset, internal = true, updateValues)
        if (hardReset) {
            updateValues["bibleAmountRead"] = 0
        }
        updateValues["totalChaptersRead"] = 0
        val db = Firebase.firestore
        db.collection("main").document(isLogged!!.uid).update(updateValues)
                .addOnSuccessListener {
                    debugLog("Successful update")
                }
                .addOnFailureListener { error ->
                    Log.w("PROFGRANT", "Failure writing to firestore", error)
                }
    }
}