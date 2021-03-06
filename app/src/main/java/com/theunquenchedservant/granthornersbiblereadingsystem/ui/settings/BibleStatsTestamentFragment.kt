package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_CHAPTERS
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.BOOK_NAMES
import com.theunquenchedservant.granthornersbiblereadingsystem.data.Books.getBooks
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.debugLog
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.extractIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Strings.capitalize
import kotlin.math.roundToInt

class BibleStatsTestamentFragment : PreferenceFragmentCompat()  {
    private lateinit var testament: String
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val mainActivity = activity as MainActivity
        val context = mainActivity.applicationContext
        val b = arguments
        testament = b?.getString("testament")!!
        mainActivity.supportActionBar?.title = "${capitalize(testament)} Testament Statistics"
        val books = getBooks(testament)!!
        val screen = preferenceManager.createPreferenceScreen(context)
        for (book in books){
            val bookPref = Preference(context)
            bookPref.title = BOOK_NAMES[book]
            bookPref.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_navigate_next_24, mainActivity.theme)
            bookPref.summary = "Loading..."
            Firebase.firestore.collection("main").document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        val currentData = it.data
                        val timesRead = extractIntPref(currentData, "${book}AmountRead")
                        val percentRead = if(extractIntPref(currentData, "${book}ChaptersRead") != 0){
                            val percentRead1 = extractIntPref(currentData, "${book}ChaptersRead").toDouble() / (BOOK_CHAPTERS[book] ?: error(""))
                            (percentRead1 * 100).roundToInt()
                        }else{
                            0
                        }
                        bookPref.summary = "$percentRead % (${extractIntPref(currentData, "${book}ChaptersRead")}/${BOOK_CHAPTERS[book]}) | Times Read: $timesRead"
                    }
                    .addOnFailureListener { error->
                        debugLog(message = "Error getting data $error")
                        bookPref.summary = "Error Loading"
                    }
            val bundle = bundleOf("book" to book)
            bookPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                mainActivity.navController.navigate(R.id.navigation_book_stats, bundle)
                false
            }
            screen.addPreference(bookPref)
        }
        preferenceScreen = screen
    }

    override fun onResume() {
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar?.title = "${capitalize(testament)} Testament Statistics"
        super.onResume()
    }
}
