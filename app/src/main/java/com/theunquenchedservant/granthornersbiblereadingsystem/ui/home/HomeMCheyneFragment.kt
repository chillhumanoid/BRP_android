package com.theunquenchedservant.granthornersbiblereadingsystem.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.theunquenchedservant.granthornersbiblereadingsystem.App
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markAll
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Marker.markSingle
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.data.ReadingLists
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.CardviewsBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.databinding.FragmentHomeMcheyneBinding
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarms
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createNotificationChannel
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setIntPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.changeVisibility
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.hideOthers
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.listSwitcher
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.resetDaily
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.ListHelpers.setVisibilities
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import java.util.*

class HomeMCheyneFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private var user: FirebaseUser? = null
    private var allowResume = true
    private var skipped = false
    private lateinit var binding: FragmentHomeMcheyneBinding
    private val viewModel: HomeMCheyneView by viewModels(
            factoryProducer =  { SavedStateViewModelFactory((activity as MainActivity).application, this) }
    )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMcheyneBinding.inflate(inflater,  container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(allowResume){
            (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
            allowResume = false
        }
    }

    override fun onPause() {
        super.onPause()
        if(!allowResume){
            allowResume = true
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(getIntPref("versionNumber") < 58){
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton(R.string.ok) { something ,_ ->
                setIntPref("versionNumber", 59)
                something.dismiss()
            }
            builder.setTitle(R.string.title_new_update)
            builder.setMessage(
                    "[ADDED] New Bible Versions (AMP, CSB, KJV, NASB95, and the NASB20)\n\n"+
                            "You can change the translation in the scripture window or under Plan Settings\n\n"+
                            "[UPDATED] You can no longer manually set a list you currently have marked as done.\n\n" +
                            "[FIXED] Song of Solomon in ESV dark mode now is in the right colors. (Thank you Meinhard)\n\n" +
                            "[Potentially FIXED] Issue where the home screen was updating when you opened the app after the lists should have moved forward"
            )
            builder.create().show()
        }
        if(getIntPref("versionNumber") == 58){
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton(R.string.ok) { something, _ ->
                setIntPref("versionNumber", 59)
                something.dismiss()
            }
            builder.setTitle(R.string.title_new_update)
            builder.setMessage(
                    "[FIXED] Acts and Revelation now work on AMP, CSB, KJV, and the NASBs\n\n" +
                            "[FIXED] Song of Solomon in ESV dark mode now is in the right colors \n\n"+
                            "Thank you Meinhard for bringing both of these to my attention."
            )
            builder.create().show()
        }
        viewModel.list1.observe(viewLifecycleOwner){
            createCard(binding.cardList1, it, R.string.title_mcheyne_list1, "mcheyne_list1", R.array.mcheyne_list1, false)
        }
        viewModel.list2.observe(viewLifecycleOwner){
            createCard(binding.cardList2, it, R.string.title_mcheyne_list2, "mcheyne_list2", R.array.mcheyne_list2, false)
        }
        viewModel.list3.observe(viewLifecycleOwner){
            createCard(binding.cardList3, it, R.string.title_mcheyne_list3, "mcheyne_list3", R.array.mcheyne_list3, false)
        }
        viewModel.list4.observe(viewLifecycleOwner){
            createCard(binding.cardList4, it, R.string.title_mcheyne_list4, "mcheyne_list4", R.array.mcheyne_list4, false)
        }
        viewModel.listsDone.observe(viewLifecycleOwner){
            val backgroundColor: String
            val allDoneBackgroundColor: String
            if(getBoolPref("darkMode", true)){
                val color = getColor(App.applicationContext(), R.color.unquenchedTextDark)
                backgroundColor = getString(R.string.btn_background_color_dark)
                allDoneBackgroundColor = getString(R.string.done_btn_background_color_dark)
                binding.materialButton.setTextColor(color)
            }else{
                val color = getColor(App.applicationContext(), R.color.unquenchedText)
                backgroundColor = getString(R.string.btn_background_color)
                allDoneBackgroundColor = getString(R.string.done_btn_background_color)
                binding.materialButton.setTextColor(color)
            }
            when(it.listsDone){
                4 -> {
                    binding.materialButton.setText(R.string.done)
                    binding.materialButton.isEnabled = true
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$allDoneBackgroundColor"))
                    binding.materialButton.backgroundTintMode= PorterDuff.Mode.ADD
                }
                0 -> {
                    binding.materialButton.setText(R.string.not_done)
                    binding.materialButton.isEnabled = true
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#$backgroundColor"))
                }
                in 1..3 -> {
                    binding.materialButton.setText(R.string.btn_mark_remaining)
                    binding.materialButton.isEnabled = true
                    val opacity = if (it.listsDone < 5){
                        100 - (it.listsDone * 5)
                    }else{
                        100 - ((it.listsDone * 5) - 5)
                    }
                    binding.materialButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#${opacity}$backgroundColor"))
                    binding.materialButton.backgroundTintMode = PorterDuff.Mode.ADD
                }
            }
        }
        if(getStringPref("planType", "horner") == "calendar" && Calendar.getInstance().get(Calendar.MONTH) == Calendar.FEBRUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 29){
            setIntPref("dailyStreak", 1)
        }else{
            createButtonListener()
        }
        createNotificationChannel()
        createAlarm("dailyCheck")
        setVisibilities(binding=null, binding, true)
        allowResume = false
        if(savedInstanceState != null) {
            createAlarms()
        }
    }

    private fun createCard(cardList: CardviewsBinding, readingLists: ReadingLists, readingString: Int, listName: String, listArray: Int, psalms:Boolean){
        val cardListRoot = cardList.root
        val enabled: Int
        val lineColor: Int
        if(getBoolPref("darkMode", true)){
            enabled = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
            lineColor = getColor(App.applicationContext(), R.color.unquenchedEmphDark)
        }else{
            enabled = getColor(App.applicationContext(), R.color.buttonBackground)
            lineColor = getColor(App.applicationContext(), R.color.unquenchedOrange)
        }
        val disabled = Color.parseColor("#00383838")
        when(readingLists.listDone){
            0 -> {
                cardListRoot.isEnabled = true
                cardListRoot.setCardBackgroundColor(enabled)
                cardList.listButtons.setBackgroundColor(enabled)
            }
            1-> {
                cardListRoot.isEnabled = true
                cardListRoot.setCardBackgroundColor(disabled)
                cardList.listButtons.setBackgroundColor(disabled)
            }
        }
        cardList.listReading.setTextColor(lineColor)
        cardList.listDone.setTextColor(lineColor)
        cardList.listRead.setTextColor(lineColor)
        cardList.buttonSeparator.setBackgroundColor(lineColor)
        cardList.lineSeparator.setBackgroundColor(lineColor)
        if(getStringPref("planType", "horner") == "calendar" && Calendar.getInstance().get(Calendar.MONTH) == Calendar.FEBRUARY && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 29){
            cardList.listReading.text = "DAY OFF"
            cardList.listTitle.text = resources.getString(readingString)
        }else{
            cardList.listReading.text = readingLists.listReading
            cardList.listTitle.text = resources.getString(readingString)
            createCardListener(cardList, listArray, psalms, "${listName}Done", listName)
        }
    }
    private fun createButtonListener() {
        val ctx = App.applicationContext()
        binding.materialButton.setOnClickListener {
            hideOthers(null, null, binding, true)
            markAll("mcheyne")
            val mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(1)
            mNotificationManager.cancel(2)
            (activity as MainActivity).navController.navigate(R.id.navigation_home)
        }
        if (getIntPref("listsDone") == 4) {
            if (getStringPref("planType", "horner") != "calendar") {
                binding.materialButton.setOnLongClickListener {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton(getString(R.string.yes)) { diag, _ ->
                        resetDaily("mcheyne")
                        (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                    }
                    builder.setNegativeButton(getString(R.string.no)) { diag, _ ->
                        diag.dismiss()
                    }
                    builder.setMessage(getString(R.string.msg_reset_all))
                    builder.setTitle(getString(R.string.title_reset_lists))
                    builder.show()
                    true
                }
            }
        }
    }

    private fun createCardListener(cardView: CardviewsBinding, arrayId: Int, psalms: Boolean, listDone: String, listName: String){
        val list = resources.getStringArray(arrayId)
        if (getIntPref(listDone) == 0){
            cardView.root.setOnClickListener {
                if (cardView.listButtons.isVisible) {
                    listSwitcher(it, getIntPref(listDone), binding.materialButton)
                } else {
                    hideOthers(cardView.root, null, binding, true)
                    cardView.listDone.setOnClickListener {
                        changeVisibility(cardView, false)
                        markSingle(listDone)
                        cardView.root.setCardBackgroundColor(Color.parseColor("#00383838"))
                        (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                    }
                    cardView.listRead.setOnClickListener {
                        lateinit var bundle: Bundle
                        val chapter: String = when (getStringPref("planType", "horner")) {
                            "horner" -> list[getIntPref(listName)]
                            "numerical" -> {
                                var index = getIntPref("mcheyne_currentDayIndex", 0)
                                while (index >= list.size) {
                                    index -= list.size
                                }
                                list[index]
                            }
                            "calendar" -> {
                                var index = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                                while (index >= list.size) {
                                    index -= list.size
                                }
                                list[index]
                            }
                            else -> list[getIntPref(listName)]
                        }
                        bundle = bundleOf("chapter" to chapter, "psalms" to false, "iteration" to 0)

                        (activity as MainActivity).navController.navigate(R.id.navigation_scripture, bundle)
                    }
                }
            }
        }else {
            if (getStringPref("planType", "horner") == "horner") {
                val enabled: Int
                if (getBoolPref("darkMode", true)) {
                    enabled = getColor(App.applicationContext(), R.color.buttonBackgroundDark)
                } else {
                    enabled = getColor(App.applicationContext(), R.color.buttonBackground)
                }
                cardView.root.setOnLongClickListener {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setPositiveButton(getString(R.string.yes)) { diag, _ ->
                        setIntPref(listDone, 0)
                        setIntPref(listName, getIntPref(listName) + 1)
                        val isLogged = FirebaseAuth.getInstance().currentUser
                        if (isLogged != null) {
                            val data = mutableMapOf<String, Any>()
                            data[listDone] = 0
                            data[listName] = getIntPref(listName)
                            db.collection("main").document(isLogged.uid).update(data)
                        }
                        cardView.root.isEnabled = true
                        cardView.root.setCardBackgroundColor(enabled)
                        cardView.listButtons.setBackgroundColor(enabled)
                        diag.dismiss()
                        (activity as MainActivity).navController.navigate(R.id.navigation_home_mcheyne)
                    }
                    builder.setNegativeButton(getString(R.string.no)) { diag, _ ->
                        diag.dismiss()
                    }
                    builder.setMessage(R.string.msg_reset_one)
                    builder.setTitle(R.string.title_reset_list)
                    builder.show()
                    true
                }
            }
        }
    }
}