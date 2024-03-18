package com.institutvidreres.winhabit.ui.config

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.institutvidreres.winhabit.R

class CustomArrayAdapter(context: Context, resource: Int, objects: Array<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        // Modifica el color del texto según el modo claro u oscuro
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
        return view
    }
}
