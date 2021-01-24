package com.theunquenchedservant.granthornersbiblereadingsystem.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity.Companion.log
import com.theunquenchedservant.granthornersbiblereadingsystem.R
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.getStringPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setBoolPref
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.SharedPref.setStringPref

class OnboardingFragmentThree : Fragment() {
    lateinit var checkboxHorner: CheckBox
    lateinit var checkboxCalendar: CheckBox
    lateinit var checkboxNumerical: CheckBox
    lateinit var titleHorner: TextView
    lateinit var titleCalendar: TextView
    lateinit var vieww: View
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        vieww = inflater.inflate(R.layout.fragment_onboarding_page_three, container, false)
        val dark = getBoolPref("darkMode", true)
        val title = vieww.findViewById<TextView>(R.id.title)
        titleHorner = vieww.findViewById<TextView>(R.id.title_horner)
        val summaryHorner = vieww.findViewById<TextView>(R.id.summary_horner)
        checkboxHorner = vieww.findViewById(R.id.checkbox_horner)
        val titleNumerical = vieww.findViewById<TextView>(R.id.title_numerical)
        val summaryNumerical = vieww.findViewById<TextView>(R.id.summary_numerical)
        checkboxNumerical = vieww.findViewById(R.id.checkbox_numerical)
        titleCalendar = vieww.findViewById<TextView>(R.id.title_calendar)
        val summaryCalendar = vieww.findViewById<TextView>(R.id.summary_calendar)
        checkboxCalendar = vieww.findViewById(R.id.checkbox_calendar)
        if(dark){
            vieww.setBackgroundColor(Color.parseColor("#121212"))
            title.setTextColor(Color.parseColor("#9cb9d3"))
            titleHorner.setTextColor(Color.parseColor("#9cb9d3"))
            titleNumerical.setTextColor(Color.parseColor("#9cb9d3"))
            titleCalendar.setTextColor(Color.parseColor("#9cb9d3"))
            summaryHorner.setTextColor(Color.parseColor("#e1e2e6"))
            summaryNumerical.setTextColor(Color.parseColor("#e1e2e6"))
            summaryCalendar.setTextColor(Color.parseColor("#e1e2e6"))
        }else{
            vieww.setBackgroundColor(Color.parseColor("#e1e2e6"))
            title.setTextColor(Color.parseColor("#b36c38"))
            titleHorner.setTextColor(Color.parseColor("#b36c38"))
            titleNumerical.setTextColor(Color.parseColor("#b36c38"))
            titleCalendar.setTextColor(Color.parseColor("#b36c38"))
            summaryHorner.setTextColor(Color.parseColor("#121212"))
            summaryNumerical.setTextColor(Color.parseColor("#121212"))
            summaryCalendar.setTextColor(Color.parseColor("#121212"))
        }
        checkboxHorner.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setBoolPref("grantHorner", true)
                setBoolPref("numericalDay", false)
                setBoolPref("calendarDay", false)
                setBoolPref("onboardingTwoDone", true)
                setStringPref("planType", "horner")
                checkboxNumerical.isChecked = false
                checkboxCalendar.isChecked = false
            }else{
                setBoolPref("onboardingTwoDone", false)
                setStringPref("planType", "")
                setBoolPref("grantHorner", false)
            }
        }
        checkboxNumerical.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setBoolPref("grantHorner", false)
                setBoolPref("numericalDay", true)
                setBoolPref("calendarDay", false)
                setBoolPref("onboardingTwoDone", true)
                setStringPref("planType", "numerical")
                checkboxHorner.isChecked = false
                checkboxCalendar.isChecked = false
            }else{
                setBoolPref("onboardingTwoDone", false)
                setStringPref("planType", "")
                setBoolPref("numericalDay", false)
            }
        }
        checkboxCalendar.setOnClickListener {
            val check = it as CheckBox
            if(check.isChecked){
                setBoolPref("grantHorner", false)
                setBoolPref("numericalDay", false)
                setBoolPref("calendarDay", true)
                setBoolPref("onboardingTwoDone", true)
                setStringPref("planType", "calendar")
                checkboxNumerical.isChecked = false
                checkboxHorner.isChecked = false
            }else{
                setBoolPref("onboardingTwoDone", false)
                setStringPref("planType", "")
                setBoolPref("calendarDay", false)
            }
        }
        return vieww
    }
    override fun onResume(){
        val planSystem = getStringPref("planSystem", "")
        if(planSystem == "pgh"){
            titleHorner.text = "Grant Horner (Recommended)"
            titleCalendar.text = "Calendar Day"
        }else if(planSystem == "mcheyne"){
            titleHorner.text = "Grant Horner"
            titleCalendar.text = "Calendar Day (Recommended)"
        }
        super.onResume()
    }
}